package com.module5.team2.controllers;

import com.module5.team2.dto.request.SendMailRequest;
import com.module5.team2.dto.request.ViolationRequest;
import com.module5.team2.dto.response.ApiResponse;
import com.module5.team2.dto.response.UserProfileResponse;
import com.module5.team2.dto.response.ViolationResponse;
import com.module5.team2.entity.UserEntity;
import com.module5.team2.service.MailService;
import com.module5.team2.service.service.StaffViolationService;
import com.module5.team2.service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/staff/violations")
@RequiredArgsConstructor
public class StaffViolationController {

    private final StaffViolationService violationService;
    private final UserService userService;
    private final MailService mailService;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createViolation(
            @Valid @RequestBody ViolationRequest request
    ) {
        violationService.createViolation(request);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message("Tạo vi phạm thành công")
                        .build()
        );
    }

//    @GetMapping
//    @PreAuthorize("hasRole('STAFF')")
//    public ResponseEntity<ApiResponse<?>> getViolations(
//            @PageableDefault(size = 5, sort = "id") Pageable pageable
//    ) {
//
//        Page<ViolationResponse> result = violationService.getViolations(pageable);
//
//        return ResponseEntity.ok(
//                ApiResponse.builder()
//                        .status(200)
//                        .message("Lấy danh sách vi phạm thành công")
//                        .data(result)
//                        .build()
//        );
//    }

    @GetMapping("/users") // done
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<?> searchUsers(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) { // Mặc định 10 record/trang

        Page<UserEntity> userPage = userService.searchUsers(keyword, pageable);

        // Map Page<Entity> sang Page<DTO> để trả về đầy đủ thông tin phân trang cho
        // Frontend
        Page<UserProfileResponse> responsePage = userPage.map(user -> UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .name(user.getName())
                .age(user.getAge())
                .role(user.getRole().name())
                .address(user.getAddress())
                .status(user.getStatus().name())
                .salary(user.getSalary())
                .build());

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message("Lấy danh sách người dùng thành công")
                        .data(responsePage)
                        .build());
    }

    @GetMapping
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<ApiResponse<?>> getViolations(
            @RequestParam(required = false) Integer customerId,
            @RequestParam(required = false) Integer supplierId,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 5, sort = "id") Pageable pageable
    ) {

        Page<ViolationResponse> result =
                violationService.searchViolations(customerId, supplierId, keyword, pageable);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message("Lấy danh sách vi phạm thành công")
                        .data(result)
                        .build()
        );
    }

//    Gửi email
    @PostMapping("/send-mail")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<ApiResponse<?>> sendMail(
            @Valid @RequestBody SendMailRequest request
    ) {

        mailService.send(
                request.getTo(),
                request.getSubject(),
                request.getContent()
        );

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message("Gửi mail thành công")
                        .build()
        );
    }
}
