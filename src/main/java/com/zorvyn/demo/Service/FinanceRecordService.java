package com.zorvyn.demo.Service;

import com.zorvyn.demo.DTO.FinanceRecordRequest;
import com.zorvyn.demo.DTO.FinanceRecordResponse;
import com.zorvyn.demo.Model.Finance;
import com.zorvyn.demo.Model.Users;
import com.zorvyn.demo.Repository.FinanceRepository;
import com.zorvyn.demo.Utils.AmountType;
import com.zorvyn.demo.Utils.ApiException;
import com.zorvyn.demo.Utils.FinanceSpecifications;
import com.zorvyn.demo.Utils.MapperUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class FinanceRecordService {
    private final FinanceRepository financeRepository;
    private final UserManagementService userManagementService;

    public FinanceRecordResponse createRecord(FinanceRecordRequest request, Long actorUserId) {
        validateDateRange(request.getTransactionDate(), request.getTransactionDate());
        Users actor = userManagementService.findUserEntity(actorUserId);

        Finance record = Finance.builder()
                .amount(request.getAmount())
                .type(request.getType())
                .category(request.getCategory().trim())
                .transactionDate(request.getTransactionDate())
                .notes(request.getNotes())
                .createdBy(actor)
                .deleted(false)
                .build();

        return MapperUtils.toFinanceRecordResponse(financeRepository.save(record));
    }

    public Page<FinanceRecordResponse> getRecords(
            AmountType type,
            String category,
            LocalDate startDate,
            LocalDate endDate,
            int page,
            int size
    ) {
        validateDateRange(startDate, endDate);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "transactionDate", "createdAt"));
        Specification<Finance> specification = Specification.allOf(
                FinanceSpecifications.notDeleted(),
                FinanceSpecifications.hasType(type),
                FinanceSpecifications.hasCategory(category),
                FinanceSpecifications.transactionDateOnOrAfter(startDate),
                FinanceSpecifications.transactionDateOnOrBefore(endDate)
        );

        return financeRepository.findAll(specification, pageable)
                .map(MapperUtils::toFinanceRecordResponse);
    }

    public FinanceRecordResponse getRecord(Long id) {
        return MapperUtils.toFinanceRecordResponse(findExistingRecord(id));
    }

    public FinanceRecordResponse updateRecord(Long id, FinanceRecordRequest request) {
        Finance record = findExistingRecord(id);

        record.setAmount(request.getAmount());
        record.setType(request.getType());
        record.setCategory(request.getCategory().trim());
        record.setTransactionDate(request.getTransactionDate());
        record.setNotes(request.getNotes());

        return MapperUtils.toFinanceRecordResponse(financeRepository.save(record));
    }

    @Transactional
    public void deleteRecord(Long id) {
        Finance record = findExistingRecord(id);
        record.setDeleted(true);
    }

    public Finance findExistingRecord(Long id) {
        Finance finance = financeRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Finance record not found"));
        if (finance.isDeleted()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Finance record not found");
        }
        return finance;
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "startDate must be before or equal to endDate");
        }
    }
}
