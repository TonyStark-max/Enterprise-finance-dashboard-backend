package com.zorvyn.demo.Service;

import com.zorvyn.demo.DTO.AuthRequest;
import com.zorvyn.demo.DTO.AuthResponse;
import com.zorvyn.demo.DTO.RegisterRequest;
import com.zorvyn.demo.DTO.UserResponse;
import com.zorvyn.demo.Model.CustomUserDetails;
import com.zorvyn.demo.Model.Users;
import com.zorvyn.demo.Repository.UserRepo;
import com.zorvyn.demo.Utils.ApiException;
import com.zorvyn.demo.Utils.MapperUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final jwtService jwtService;
    private final UserRepo userRepo;
    private final UserManagementService userManagementService;

    public UserResponse register(RegisterRequest request) {
        return userManagementService.registerPublicUser(
                request.getFullName(),
                request.getEmail(),
                request.getPassword()
        );
    }

    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail().trim().toLowerCase(), request.getPassword())
        );

        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        Users user = userRepo.findByEmail(principal.getEmail())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        if (!user.isActive()) {
            throw new ApiException(HttpStatus.FORBIDDEN, "User account is inactive");
        }

        return AuthResponse.builder()
                .token(jwtService.generateToken(principal))
                .user(MapperUtils.toUserResponse(user))
                .build();
    }
}
