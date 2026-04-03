package com.zorvyn.demo.Service;

import com.zorvyn.demo.DTO.CategoryTotalResponse;
import com.zorvyn.demo.DTO.DashboardSummaryResponse;
import com.zorvyn.demo.DTO.FinanceRecordResponse;
import com.zorvyn.demo.DTO.TrendPointResponse;
import com.zorvyn.demo.Model.Finance;
import com.zorvyn.demo.Repository.FinanceRepository;
import com.zorvyn.demo.Utils.AmountType;
import com.zorvyn.demo.Utils.MapperUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final FinanceRepository financeRepository;

    public DashboardSummaryResponse getSummary(LocalDate startDate, LocalDate endDate) {
        LocalDate effectiveEnd = endDate != null ? endDate : LocalDate.now();
        LocalDate effectiveStart = startDate != null ? startDate : effectiveEnd.minusMonths(5).withDayOfMonth(1);
        if (effectiveStart.isAfter(effectiveEnd)) {
            throw new com.zorvyn.demo.Utils.ApiException(org.springframework.http.HttpStatus.BAD_REQUEST, "startDate must be before or equal to endDate");
        }

        List<Finance> records = financeRepository.findByDeletedFalseAndTransactionDateBetween(effectiveStart, effectiveEnd);
        BigDecimal totalIncome = sumByType(records, AmountType.INCOME);
        BigDecimal totalExpense = sumByType(records, AmountType.EXPENSE);

        return DashboardSummaryResponse.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .netBalance(totalIncome.subtract(totalExpense))
                .totalRecords(records.size())
                .incomeRecordCount(records.stream().filter(record -> record.getType() == AmountType.INCOME).count())
                .expenseRecordCount(records.stream().filter(record -> record.getType() == AmountType.EXPENSE).count())
                .categoryTotals(buildCategoryTotals(records))
                .monthlyTrends(buildMonthlyTrends(records, effectiveStart, effectiveEnd))
                .recentActivity(financeRepository.findTop5ByDeletedFalseOrderByTransactionDateDescCreatedAtDesc().stream().map(MapperUtils::toFinanceRecordResponse).toList())
                .build();
    }

    private BigDecimal sumByType(List<Finance> records, AmountType type) {
        return records.stream()
                .filter(record -> record.getType() == type)
                .map(Finance::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<CategoryTotalResponse> buildCategoryTotals(List<Finance> records) {
        Map<String, BigDecimal> grouped = records.stream()
                .collect(Collectors.groupingBy(
                        Finance::getCategory,
                        Collectors.mapping(Finance::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        return grouped.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue(Comparator.reverseOrder()))
                .map(entry -> new CategoryTotalResponse(entry.getKey(), entry.getValue()))
                .toList();
    }

    private List<TrendPointResponse> buildMonthlyTrends(List<Finance> records, LocalDate startDate, LocalDate endDate) {
        Map<YearMonth, List<Finance>> byMonth = records.stream()
                .collect(Collectors.groupingBy(record -> YearMonth.from(record.getTransactionDate())));

        List<TrendPointResponse> trends = new ArrayList<>();
        YearMonth cursor = YearMonth.from(startDate);
        YearMonth endMonth = YearMonth.from(endDate);
        while (!cursor.isAfter(endMonth)) {
            List<Finance> monthRecords = byMonth.getOrDefault(cursor, List.of());
            BigDecimal income = sumByType(monthRecords, AmountType.INCOME);
            BigDecimal expense = sumByType(monthRecords, AmountType.EXPENSE);
            trends.add(new TrendPointResponse(cursor.toString(), income, expense, income.subtract(expense)));
            cursor = cursor.plusMonths(1);
        }
        return trends;
    }
}
