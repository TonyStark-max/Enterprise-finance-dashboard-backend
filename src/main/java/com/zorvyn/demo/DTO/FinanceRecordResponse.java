package com.zorvyn.demo.DTO;

import com.zorvyn.demo.Utils.AmountType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
public class FinanceRecordResponse {
    private Long id;
    private BigDecimal amount;
    private AmountType type;
    private String category;
    private LocalDate transactionDate;
    private String notes;
    private Long createdByUserId;
    private String createdByEmail;
    private Instant createdAt;
    private Instant updatedAt;
}
