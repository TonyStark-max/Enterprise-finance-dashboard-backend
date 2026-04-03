package com.zorvyn.demo.DTO;

import com.zorvyn.demo.Utils.Roles;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private Roles role;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
}
