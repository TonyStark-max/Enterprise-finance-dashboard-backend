package com.zorvyn.demo.Config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseRepairRunner implements ApplicationRunner {
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            String databaseProduct = connection.getMetaData().getDatabaseProductName();
            if (!"PostgreSQL".equalsIgnoreCase(databaseProduct)) {
                return;
            }
        }

        if (!tableExists("users")) {
            return;
        }

        repairUsersTable();
    }

    private void repairUsersTable() {
        log.info("Running PostgreSQL compatibility repair for users table");
        String identifierColumn = resolveUserIdentifierColumn();

        jdbcTemplate.execute("ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check");

        executeSafely("ALTER TABLE users ADD COLUMN IF NOT EXISTS full_name varchar(255)");
        executeSafely("ALTER TABLE users ADD COLUMN IF NOT EXISTS active boolean");
        executeSafely("ALTER TABLE users ADD COLUMN IF NOT EXISTS created_at timestamp(6) with time zone");
        executeSafely("ALTER TABLE users ADD COLUMN IF NOT EXISTS updated_at timestamp(6) with time zone");

        jdbcTemplate.update("UPDATE users SET role = 'VIEWER' WHERE role IS NULL OR role = 'USER'");
        if (identifierColumn != null) {
            jdbcTemplate.update(
                    "UPDATE users SET full_name = COALESCE(full_name, " + identifierColumn + ") WHERE full_name IS NULL"
            );
        }
        jdbcTemplate.update("UPDATE users SET active = COALESCE(active, true) WHERE active IS NULL");
        jdbcTemplate.update("UPDATE users SET created_at = COALESCE(created_at, CURRENT_TIMESTAMP) WHERE created_at IS NULL");
        jdbcTemplate.update("UPDATE users SET updated_at = COALESCE(updated_at, CURRENT_TIMESTAMP) WHERE updated_at IS NULL");

        jdbcTemplate.execute("""
                ALTER TABLE users
                ADD CONSTRAINT users_role_check
                CHECK (role IN ('VIEWER', 'ANALYST', 'ADMIN'))
                """);
    }

    private boolean tableExists(String tableName) {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM information_schema.tables
                WHERE table_schema = 'public' AND table_name = ?
                """,
                Integer.class,
                tableName
        );
        return count != null && count > 0;
    }

    private boolean columnExists(String tableName, String columnName) {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM information_schema.columns
                WHERE table_schema = 'public' AND table_name = ? AND column_name = ?
                """,
                Integer.class,
                tableName,
                columnName
        );
        return count != null && count > 0;
    }

    private String resolveUserIdentifierColumn() {
        if (columnExists("users", "username")) {
            return "username";
        }
        if (columnExists("users", "email")) {
            return "email";
        }

        log.warn("Skipping users full_name backfill because neither username nor email column exists");
        return null;
    }

    private void executeSafely(String sql) {
        try {
            jdbcTemplate.execute(sql);
        } catch (Exception ex) {
            log.debug("Skipped SQL during repair: {}", sql, ex);
        }
    }
}
