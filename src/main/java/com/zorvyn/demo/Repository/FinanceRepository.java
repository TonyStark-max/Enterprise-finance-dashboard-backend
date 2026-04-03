package com.zorvyn.demo.Repository;

import com.zorvyn.demo.Model.Finance;
import com.zorvyn.demo.Utils.AmountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;

public interface FinanceRepository extends JpaRepository<Finance, Long>, JpaSpecificationExecutor<Finance> {
    List<Finance> findByDeletedFalseAndTransactionDateBetween(LocalDate startDate, LocalDate endDate);
    List<Finance> findTop5ByDeletedFalseOrderByTransactionDateDescCreatedAtDesc();
    long countByDeletedFalseAndType(AmountType type);
}
