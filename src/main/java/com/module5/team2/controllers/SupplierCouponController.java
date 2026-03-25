package com.module5.team2.controllers;

import com.module5.team2.dto.request.CouponRequest;
import com.module5.team2.dto.response.ApiResponse;
import com.module5.team2.dto.response.CouponResponse;
import com.module5.team2.service.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/supplier/coupons")
@RequiredArgsConstructor
public class SupplierCouponController {
    private final CouponService couponService;

    @PostMapping
    public ResponseEntity<ApiResponse<CouponResponse>> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CouponRequest request
    ) {
        CouponResponse data = couponService.create(userDetails.getUsername(), request);

        return ResponseEntity.status(201).body(
                ApiResponse.<CouponResponse>builder()
                        .status(201)
                        .message("Tạo mã thành công")
                        .data(data)
                        .build()
        );
    }

    // ================= GET LIST =================
    @GetMapping
    public ResponseEntity<ApiResponse<Page<CouponResponse>>> getCoupons(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) Boolean status
    ) {
        Page<CouponResponse> data =
                couponService.getCoupons(userDetails.getUsername(), page, size, keyword, status);

        return ResponseEntity.ok(
                ApiResponse.<Page<CouponResponse>>builder()
                        .status(200)
                        .message("Lấy danh sách thành công")
                        .data(data)
                        .build()
        );
    }

    // ================= TOGGLE =================
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<Void>> toggle(@PathVariable Long id) {

        couponService.toggle(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(200)
                        .message("Đã thay đổi trạng thái")
                        .data(null)
                        .build()
        );
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {

        couponService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(200)
                        .message("Xóa thành công")
                        .data(null)
                        .build()
        );
    }
}
