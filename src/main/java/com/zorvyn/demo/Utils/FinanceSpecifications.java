package com.zorvyn.demo.Utils;

import com.zorvyn.demo.Model.Finance;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class FinanceSpecifications {
    private FinanceSpecifications() {
    }

    public static Specification<Finance> notDeleted() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isFalse(root.get("deleted"));
    }

    public static Specification<Finance> hasCategory(String category) {
        return (root, query, criteriaBuilder) ->
                category == null || category.isBlank()
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(criteriaBuilder.lower(root.get("category")), category.trim().toLowerCase());
    }

    public static Specification<Finance> hasType(AmountType type) {
        return (root, query, criteriaBuilder) ->
                type == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("type"), type);
    }

    public static Specification<Finance> transactionDateOnOrAfter(LocalDate startDate) {
        return (root, query, criteriaBuilder) ->
                startDate == null ? criteriaBuilder.conjunction() : criteriaBuilder.greaterThanOrEqualTo(root.get("transactionDate"), startDate);
    }

    public static Specification<Finance> transactionDateOnOrBefore(LocalDate endDate) {
        return (root, query, criteriaBuilder) ->
                endDate == null ? criteriaBuilder.conjunction() : criteriaBuilder.lessThanOrEqualTo(root.get("transactionDate"), endDate);
    }
}
