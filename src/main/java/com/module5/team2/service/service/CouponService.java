package com.module5.team2.service.service;

import com.module5.team2.dto.request.CouponRequest;
import com.module5.team2.dto.response.CouponResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CouponService {

    CouponResponse create(String username, CouponRequest request);

    Page<CouponResponse> getCoupons(
            String username,
            int page,
            int size,
            String keyword,
            Boolean status
    );


    void toggle(Long id);

    void delete(Long id);
}
