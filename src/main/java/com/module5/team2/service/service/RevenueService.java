package com.module5.team2.service.service;

import com.module5.team2.dto.response.ShopRevenueResponse;
import org.springframework.data.domain.Page;

public interface RevenueService {
    Page<ShopRevenueResponse> getRevenue(int page, int size, String sortDir);
}