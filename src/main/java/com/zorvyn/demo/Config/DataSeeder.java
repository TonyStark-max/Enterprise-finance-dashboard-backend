package com.zorvyn.demo.Config;

import com.zorvyn.demo.Model.Finance;
import com.zorvyn.demo.Model.Users;
import com.zorvyn.demo.Repository.FinanceRepository;
import com.zorvyn.demo.Repository.UserRepo;
import com.zorvyn.demo.Utils.AmountType;
import com.zorvyn.demo.Utils.Roles;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner seedDefaultData(UserRepo userRepo, FinanceRepository financeRepository) {
        return args -> {
            if (userRepo.count() > 0) {
                return;
            }

            Users admin = userRepo.save(Users.builder()
                    .fullName("System Admin")
                    .email("admin@finance.local")
                    .password(passwordEncoder.encode("Admin@123"))
                    .role(Roles.ADMIN)
                    .active(true)
                    .build());

            userRepo.save(Users.builder()
                    .fullName("Insight Analyst")
                    .email("analyst@finance.local")
                    .password(passwordEncoder.encode("Analyst@123"))
                    .role(Roles.ANALYST)
                    .active(true)
                    .build());

            userRepo.save(Users.builder()
                    .fullName("Dashboard Viewer")
                    .email("viewer@finance.local")
                    .password(passwordEncoder.encode("Viewer@123"))
                    .role(Roles.VIEWER)
                    .active(true)
                    .build());

            financeRepository.saveAll(List.of(
                    Finance.builder().amount(new BigDecimal("5200.00")).type(AmountType.INCOME).category("Salary").transactionDate(LocalDate.now().minusDays(20)).notes("Monthly salary").createdBy(admin).build(),
                    Finance.builder().amount(new BigDecimal("850.75")).type(AmountType.EXPENSE).category("Rent").transactionDate(LocalDate.now().minusDays(18)).notes("Apartment rent").createdBy(admin).build(),
                    Finance.builder().amount(new BigDecimal("420.25")).type(AmountType.EXPENSE).category("Groceries").transactionDate(LocalDate.now().minusDays(12)).notes("Weekly household supplies").createdBy(admin).build(),
                    Finance.builder().amount(new BigDecimal("1200.00")).type(AmountType.INCOME).category("Freelance").transactionDate(LocalDate.now().minusDays(9)).notes("Side project payment").createdBy(admin).build(),
                    Finance.builder().amount(new BigDecimal("245.40")).type(AmountType.EXPENSE).category("Utilities").transactionDate(LocalDate.now().minusDays(5)).notes("Electricity and internet").createdBy(admin).build()
            ));
        };
    }
}
