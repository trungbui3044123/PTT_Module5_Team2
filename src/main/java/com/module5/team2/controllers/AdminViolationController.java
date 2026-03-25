package com.module5.team2.controllers;

import com.module5.team2.dto.response.ApiResponse;
import com.module5.team2.service.service.AdminViolationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/violations")
@RequiredArgsConstructor
public class AdminViolationController {

    private final AdminViolationService service;

    @GetMapping("/staffs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> getStaffs() {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message("Lấy danh sách staff thành công")
                        .data(service.getAllStaff())
                        .build()
        );
    }

    @GetMapping("/staff/{staffId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> getViolationsByStaff(
            @PathVariable Long staffId
    ) {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message("Lấy danh sách vi phạm theo staff thành công")
                        .data(service.getViolationsByStaff(staffId))
                        .build()
        );
    }
}
