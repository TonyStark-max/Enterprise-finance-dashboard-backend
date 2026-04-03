package com.zorvyn.demo.Controller;

import com.zorvyn.demo.DTO.FinanceRecordRequest;
import com.zorvyn.demo.DTO.FinanceRecordResponse;
import com.zorvyn.demo.Model.CustomUserDetails;
import com.zorvyn.demo.Service.FinanceRecordService;
import com.zorvyn.demo.Utils.AmountType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/records")
@RequiredArgsConstructor
@Tag(name = "Finance Records", description = "Finance record CRUD and filtered listing")
@SecurityRequirement(name = "bearerAuth")
public class FinanceController {
    private final FinanceRecordService financeRecordService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Create a finance record", description = "Admin-only endpoint to create a finance record")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Record created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content(schema = @Schema(implementation = com.zorvyn.demo.DTO.ApiErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = com.zorvyn.demo.DTO.ApiErrorResponse.class)))
    })
    public ResponseEntity<FinanceRecordResponse> createRecord(
            @Valid @RequestBody FinanceRecordRequest request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(financeRecordService.createRecord(request, user.getUserId()));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ANALYST', 'VIEWER')")
    @Operation(summary = "List finance records", description = "Returns paginated finance records with optional filters")
    public ResponseEntity<Page<FinanceRecordResponse>> getRecords(
            @Parameter(description = "Optional filter by amount type: INCOME or EXPENSE")
            @RequestParam(required = false) AmountType type,
            @Parameter(description = "Optional filter by category")
            @RequestParam(required = false) String category,
            @Parameter(description = "Optional start date filter in yyyy-MM-dd")
            @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "Optional end date filter in yyyy-MM-dd")
            @RequestParam(required = false) LocalDate endDate,
            @Parameter(description = "Zero-based page number")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(financeRecordService.getRecords(type, category, startDate, endDate, page, size));
    }

    @GetMapping("/get/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ANALYST', 'VIEWER')")
    @Operation(summary = "Get a finance record", description = "Returns a finance record by id")
    public ResponseEntity<FinanceRecordResponse> getRecord(@PathVariable Long id) {
        return ResponseEntity.ok(financeRecordService.getRecord(id));
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Update a finance record", description = "Admin-only endpoint to update a finance record")
    public ResponseEntity<FinanceRecordResponse> updateRecord(
            @PathVariable Long id,
            @Valid @RequestBody FinanceRecordRequest request
    ) {
        return ResponseEntity.ok(financeRecordService.updateRecord(id, request));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Delete a finance record", description = "Admin-only endpoint to soft delete a finance record")
    public ResponseEntity<Void> deleteRecord(@PathVariable Long id) {
        financeRecordService.deleteRecord(id);
        return ResponseEntity.noContent().build();
    }
}
