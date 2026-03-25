package com.module5.team2.service.serviceImpl;

import com.module5.team2.dto.request.ViolationRequest;
import com.module5.team2.dto.response.ViolationResponse;
import com.module5.team2.entity.Note;
import com.module5.team2.entity.UserEntity;
import com.module5.team2.exception.BusinessException;
import com.module5.team2.repository.NoteRepository;
import com.module5.team2.repository.UserRepository;
import com.module5.team2.repository.ViolationRepository;
import com.module5.team2.service.service.StaffViolationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StaffViolationServiceImpl implements StaffViolationService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final ViolationRepository violationRepository;

    @Override
    public void createViolation(ViolationRequest request) {

        UserEntity supplier = userRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new BusinessException("Supplier không tồn tại"));

        UserEntity customer = null;

        if (request.getUserId() != null) {
            customer = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new BusinessException("Customer không tồn tại"));
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity staff = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("Staff không tồn tại"));

        Note note = Note.builder()
                .supplier(supplier)
                .customer(customer)
                .staff(staff)
                .violation(request.getViolation())
                .status(request.getStatus())
                .createdAt(request.getCreatedAt())
                .build();

        noteRepository.save(note);
    }

    @Override
    public Page<ViolationResponse> getViolations(Pageable pageable) {

        Page<Note> page = violationRepository.findAll(pageable);

        return page.map(note -> ViolationResponse.builder()
                .id(note.getId())
                .customerName(
                        note.getCustomer() != null
                                ? note.getCustomer().getUsername()
                                : null
                )
                .supplierName(
                        note.getSupplier() != null
                                ? note.getSupplier().getUsername()
                                : null
                )
                .violation(note.getViolation())
                .status(note.getStatus().name())
                .createdAt(note.getCreatedAt())
                .build()
        );
    }

    @Override
    public Page<ViolationResponse> searchViolations(
            Integer customerId,
            Integer supplierId,
            String keyword,
            Pageable pageable
    ) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity staff = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("Staff không tồn tại"));

        Page<Note> page = violationRepository.searchViolationsByStaff(
                Long.valueOf(staff.getId()),
                customerId,
                supplierId,
                keyword,
                pageable
        );

        return page.map(note -> ViolationResponse.builder()
                .id(note.getId())
                .customerName(
                        note.getCustomer() != null
                                ? note.getCustomer().getUsername()
                                : null
                )
                .supplierName(
                        note.getSupplier() != null
                                ? note.getSupplier().getUsername()
                                : null
                )
                .staffName(note.getStaff().getUsername())
                .violation(note.getViolation())
                .status(note.getStatus().name())
                .createdAt(note.getCreatedAt())
                .build()
        );
    }
}
