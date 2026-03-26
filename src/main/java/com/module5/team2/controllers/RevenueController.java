package com.module5.team2.controllers;

import com.module5.team2.dto.response.ApiResponse;
import com.module5.team2.dto.response.ShopRevenueResponse;
import com.module5.team2.entity.UserEntity;
import com.module5.team2.repository.OrderRepository;
import com.module5.team2.repository.UserRepository;
import com.module5.team2.service.service.RevenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/revenue")
@RequiredArgsConstructor
public class RevenueController {

    private final RevenueService revenueService;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    // 🔥 ADMIN - xem tất cả shop
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<?>> getRevenue(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sort
    ) {

        Page<ShopRevenueResponse> result =
                revenueService.getRevenue(page, size, sort);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message("Lấy danh sách doanh thu thành công")
                        .data(result)
                        .build()
        );
    }

    @GetMapping("/supplier")
    public ResponseEntity<ApiResponse<?>> getSupplierRevenue(Authentication authentication) {

        String username = authentication.getName();

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        BigDecimal total = orderRepository.getRevenueBySupplier(user.getId());
        BigDecimal adminRevenue = total.multiply(new BigDecimal("0.03"));

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message("Lấy doanh thu supplier thành công")
                        .data(
                                Map.of(
                                        "totalRevenue", total
                                )
                        )
                        .build()
        );
    }
}