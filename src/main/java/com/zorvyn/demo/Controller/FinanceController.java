package com.zorvyn.demo.Controller;

import com.zorvyn.demo.DTO.FinanceRecordRequest;
import com.zorvyn.demo.DTO.FinanceRecordResponse;
import com.zorvyn.demo.Model.CustomUserDetails;
import com.zorvyn.demo.Service.FinanceRecordService;
import com.zorvyn.demo.Utils.AmountType;
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
public class FinanceController {
    private final FinanceRecordService financeRecordService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<FinanceRecordResponse> createRecord(
            @Valid @RequestBody FinanceRecordRequest request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(financeRecordService.createRecord(request, user.getUserId()));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ANALYST', 'VIEWER')")
    public ResponseEntity<Page<FinanceRecordResponse>> getRecords(
            @RequestParam(required = false) AmountType type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(financeRecordService.getRecords(type, category, startDate, endDate, page, size));
    }

    @GetMapping("/get/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ANALYST', 'VIEWER')")
    public ResponseEntity<FinanceRecordResponse> getRecord(@PathVariable Long id) {
        return ResponseEntity.ok(financeRecordService.getRecord(id));
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<FinanceRecordResponse> updateRecord(
            @PathVariable Long id,
            @Valid @RequestBody FinanceRecordRequest request
    ) {
        return ResponseEntity.ok(financeRecordService.updateRecord(id, request));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteRecord(@PathVariable Long id) {
        financeRecordService.deleteRecord(id);
        return ResponseEntity.noContent().build();
    }
}
