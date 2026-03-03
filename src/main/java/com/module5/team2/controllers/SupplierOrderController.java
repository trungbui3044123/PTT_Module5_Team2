package com.module5.team2.controllers;

import com.module5.team2.dto.request.RejectOrderRequest;
import com.module5.team2.dto.response.ApiResponse;
import com.module5.team2.dto.response.OrderResponse;
import com.module5.team2.enums.OrderStatus;
import com.module5.team2.security.jwt.CustomUserDetails;
import com.module5.team2.service.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/supplier/orders")
@RequiredArgsConstructor
public class SupplierOrderController {
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getOrders(
            @RequestParam(required = false) OrderStatus status,
            Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails user
    ) {

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message("Danh sách đơn hàng")
                        .data(orderService.getSupplierOrders(user.getId(), status, pageable))
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails user
    ) {

        return ResponseEntity.ok(
                ApiResponse.<OrderResponse>builder()
                        .status(200)
                        .message("Chi tiết đơn hàng")
                        .data(orderService.getOrderDetail(id, user.getId()))
                        .build()
        );
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<ApiResponse<?>> confirm(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails user
    ) {

        orderService.confirmOrder(id, user.getId());

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message("Xác nhận đơn hàng thành công")
                        .build()
        );
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<?>> reject(
            @PathVariable Long id,
            @Valid @RequestBody RejectOrderRequest request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {

        orderService.rejectOrder(id, user.getId(), request.getReason());

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message("Từ chối đơn hàng thành công")
                        .build()
        );
    }

    @GetMapping("/revenue")
    public ResponseEntity<ApiResponse<BigDecimal>> revenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime start,

            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime end,

            @AuthenticationPrincipal CustomUserDetails user
    ) {

        return ResponseEntity.ok(
                ApiResponse.<BigDecimal>builder()
                        .status(200)
                        .message("Doanh thu")
                        .data(orderService.calculateRevenue(user.getId(), start, end))
                        .build()
        );
    }
}
