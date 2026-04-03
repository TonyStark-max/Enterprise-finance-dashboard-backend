package com.zorvyn.demo.DTO;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class DashboardSummaryResponse {
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal netBalance;
    private long totalRecords;
    private long incomeRecordCount;
    private long expenseRecordCount;
    private List<CategoryTotalResponse> categoryTotals;
    private List<TrendPointResponse> monthlyTrends;
    private List<FinanceRecordResponse> recentActivity;
}
