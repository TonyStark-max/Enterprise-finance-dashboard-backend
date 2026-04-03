package com.zorvyn.demo.Service;

import com.zorvyn.demo.DTO.UserCreateRequest;
import com.zorvyn.demo.DTO.UserResponse;
import com.zorvyn.demo.DTO.UserUpdateRequest;
import com.zorvyn.demo.Model.Users;
import com.zorvyn.demo.Repository.UserRepo;
import com.zorvyn.demo.Utils.ApiException;
import com.zorvyn.demo.Utils.MapperUtils;
import com.zorvyn.demo.Utils.Roles;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserManagementService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserResponse registerPublicUser(String fullName, String email, String password) {
        String normalizedEmail = email.trim().toLowerCase();
        if (userRepo.existsByEmail(normalizedEmail)) {
            throw new ApiException(HttpStatus.CONFLICT, "User with this email already exists");
        }

        Users user = Users.builder()
                .fullName(fullName.trim())
                .email(normalizedEmail)
                .password(passwordEncoder.encode(password))
                .role(Roles.VIEWER)
                .active(true)
                .build();

        return MapperUtils.toUserResponse(userRepo.save(user));
    }

    public UserResponse createUser(UserCreateRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();
        if (userRepo.existsByEmail(normalizedEmail)) {
            throw new ApiException(HttpStatus.CONFLICT, "User with this email already exists");
        }

        Users user = Users.builder()
                .fullName(request.getFullName().trim())
                .email(normalizedEmail)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .active(request.isActive())
                .build();

        return MapperUtils.toUserResponse(userRepo.save(user));
    }

    public List<UserResponse> getAllUsers() {
        return userRepo.findAll().stream()
                .map(MapperUtils::toUserResponse)
                .toList();
    }

    public UserResponse getUser(Long id) {
        return MapperUtils.toUserResponse(findUserEntity(id));
    }

    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        Users user = findUserEntity(id);

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            user.setFullName(request.getFullName().trim());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }

        return MapperUtils.toUserResponse(userRepo.save(user));
    }

    public Users findUserEntity(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
    }
}
