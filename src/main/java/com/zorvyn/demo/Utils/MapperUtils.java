package com.zorvyn.demo.Utils;

import com.zorvyn.demo.DTO.FinanceRecordResponse;
import com.zorvyn.demo.DTO.UserResponse;
import com.zorvyn.demo.Model.Finance;
import com.zorvyn.demo.Model.Users;

public final class MapperUtils {
    private MapperUtils() {
    }

    public static UserResponse toUserResponse(Users user) {
        String displayName = user.getFullName() != null && !user.getFullName().isBlank()
                ? user.getFullName()
                : user.getEmail();

        return UserResponse.builder()
                .id(user.getId())
                .fullName(displayName)
                .email(user.getEmail())
                .role(user.getRole())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public static FinanceRecordResponse toFinanceRecordResponse(Finance finance) {
        return FinanceRecordResponse.builder()
                .id(finance.getId())
                .amount(finance.getAmount())
                .type(finance.getType())
                .category(finance.getCategory())
                .transactionDate(finance.getTransactionDate())
                .notes(finance.getNotes())
                .createdByUserId(finance.getCreatedBy().getId())
                .createdByEmail(finance.getCreatedBy().getEmail())
                .createdAt(finance.getCreatedAt())
                .updatedAt(finance.getUpdatedAt())
                .build();
    }
}
