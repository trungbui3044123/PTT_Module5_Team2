package com.module5.team2.service.service;

import com.module5.team2.dto.request.ViolationRequest;
import com.module5.team2.dto.response.ViolationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StaffViolationService {
    void createViolation(ViolationRequest request);
    Page<ViolationResponse> getViolations(Pageable pageable);
    Page<ViolationResponse> searchViolations(
            Integer customerId,
            Integer supplierId,
            String keyword,
            Pageable pageable
    );
}
