package com.module5.team2.controllers;

import com.module5.team2.dto.response.ApiResponse;
import com.module5.team2.security.jwt.CustomUserDetails;
import com.module5.team2.service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getMyNotifications(
            Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails user
    ) {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message("Danh sách thông báo")
                        .data(notificationService.getUserNotifications(user.getId(), pageable))
                        .build()
        );
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<?>> markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails user
    ) {

        notificationService.markAsRead(id, user.getId());

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message("Đã đánh dấu đã đọc")
                        .build()
        );
    }

    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<?>> markAllAsRead(
            @AuthenticationPrincipal CustomUserDetails user
    ) {

        notificationService.markAllAsRead(user.getId());

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message("Đã đánh dấu tất cả là đã đọc")
                        .build()
        );
    }
}
