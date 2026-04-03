package com.zorvyn.demo.Controller;

import com.zorvyn.demo.DTO.DashboardSummaryResponse;
import com.zorvyn.demo.Service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Analytics summary endpoints")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/summary")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ANALYST', 'VIEWER')")
    @Operation(summary = "Get dashboard summary", description = "Returns totals, counts, category summaries, monthly trends, and recent activity")
    public ResponseEntity<DashboardSummaryResponse> getSummary(
            @Parameter(description = "Optional start date filter in yyyy-MM-dd")
            @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "Optional end date filter in yyyy-MM-dd")
            @RequestParam(required = false) LocalDate endDate
    ) {
        return ResponseEntity.ok(dashboardService.getSummary(startDate, endDate));
    }
}
