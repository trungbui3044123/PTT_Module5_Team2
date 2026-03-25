package com.module5.team2.service.serviceImpl;

import com.module5.team2.dto.response.ShopRevenueResponse;
import com.module5.team2.repository.OrderRepository;
import com.module5.team2.service.service.RevenueService;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RevenueServiceImpl implements RevenueService {

    private final OrderRepository orderRepository;

    public RevenueServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Page<ShopRevenueResponse> getRevenue(int page, int size, String sortDir) {

        List<Object[]> raw = orderRepository.getShopRevenueRaw();

        List<ShopRevenueResponse> list = raw.stream().map(r -> {
            BigDecimal total = (BigDecimal) r[2];

            return ShopRevenueResponse.builder()
                    .id((Integer) r[0])
                    .name((String) r[1])
                    .totalRevenue(total)
                    .adminRevenue(total.multiply(new BigDecimal("0.03")))
                    .build();
        }).collect(Collectors.toList()); // 🔥 FIX

        // sort
        list.sort((a, b) -> {
            if ("asc".equalsIgnoreCase(sortDir)) {
                return a.getTotalRevenue().compareTo(b.getTotalRevenue());
            } else {
                return b.getTotalRevenue().compareTo(a.getTotalRevenue());
            }
        });

        // pagination
        int start = page * size;
        int end = Math.min(start + size, list.size());

        List<ShopRevenueResponse> pageContent =
                start >= list.size() ? List.of() : list.subList(start, end);

        return new PageImpl<>(pageContent, PageRequest.of(page, size), list.size());
    }
}