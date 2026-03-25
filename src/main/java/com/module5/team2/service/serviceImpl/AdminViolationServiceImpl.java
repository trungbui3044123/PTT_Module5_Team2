package com.module5.team2.service.serviceImpl;

import com.module5.team2.dto.response.ViolationResponse;
import com.module5.team2.entity.Note;
import com.module5.team2.entity.UserEntity;
import com.module5.team2.enums.Role;
import com.module5.team2.exception.BusinessException;
import com.module5.team2.repository.UserRepository;
import com.module5.team2.repository.ViolationRepository;
import com.module5.team2.service.service.AdminViolationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminViolationServiceImpl implements AdminViolationService {

    private final UserRepository userRepository;
    private final ViolationRepository violationRepository;

    @Override
    public List<UserEntity> getAllStaff() {
        return userRepository.findByRole(Role.STAFF);
    }

    @Override
    public List<ViolationResponse> getViolationsByStaff(Long staffId) {

        UserEntity staff = userRepository.findById(staffId.intValue())
                .orElseThrow(() -> new BusinessException("Staff không tồn tại"));

        List<Note> list = violationRepository.findByStaffId(staffId);

        return list.stream().map(note -> ViolationResponse.builder()
                .id(note.getId())
                .customerName(note.getCustomer() != null ? note.getCustomer().getUsername() : null)
                .supplierName(note.getSupplier().getUsername())
                .staffName(note.getStaff().getUsername())
                .violation(note.getViolation())
                .status(note.getStatus().name())
                .createdAt(note.getCreatedAt())
                .build()
        ).toList();
    }
}
