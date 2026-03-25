package com.module5.team2.controllers;

import com.module5.team2.dto.response.CouponResponse;
import com.module5.team2.repository.CouponRepository;
import com.module5.team2.service.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @GetMapping
    public List<CouponResponse> getCouponsBySupplier(
            @RequestParam Long supplierId
    ) {
        return couponService.getCouponsBySupplierId(supplierId);
    }

}
