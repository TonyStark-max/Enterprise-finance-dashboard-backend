package com.zorvyn.demo.Controller;

import com.zorvyn.demo.DTO.UserCreateRequest;
import com.zorvyn.demo.DTO.UserResponse;
import com.zorvyn.demo.DTO.UserUpdateRequest;
import com.zorvyn.demo.Service.UserManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Admin-only user management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    private final UserManagementService userManagementService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Create a user", description = "Creates a new user with the specified role and status")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content(schema = @Schema(implementation = com.zorvyn.demo.DTO.ApiErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = com.zorvyn.demo.DTO.ApiErrorResponse.class)))
    })
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userManagementService.createUser(request));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "List all users", description = "Returns all users in the system")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userManagementService.getAllUsers());
    }

    @GetMapping("/get/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Get a user by id", description = "Returns a single user by identifier")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userManagementService.getUser(id));
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Update a user", description = "Updates name, role, and active status for a user")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userManagementService.updateUser(id, request));
    }
}
