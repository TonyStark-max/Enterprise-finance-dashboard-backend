package com.zorvyn.demo.Repository;

import com.zorvyn.demo.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<Users,Long> {
    Optional<Users> findByEmail(String email);
    boolean existsByEmail(String email);
}
