package com.zorvyn.demo.DTO;

import com.zorvyn.demo.Utils.Roles;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateRequest {
    @Size(max = 100, message = "Full name must be at most 100 characters")
    private String fullName;

    private Roles role;

    private Boolean active;
}
