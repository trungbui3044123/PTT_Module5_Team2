package com.module5.team2.service.service;

import com.module5.team2.dto.response.OrderResponse;
import com.module5.team2.dto.response.OrderSummaryResponse;
import com.module5.team2.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface OrderService {
    Page<OrderSummaryResponse> getSupplierOrders(
            Integer supplierId,
            OrderStatus status,
            Pageable pageable
    );

    OrderResponse getOrderDetail(Long orderId, Integer supplierId);

    void confirmOrder(Long orderId, Integer supplierId);

    void rejectOrder(Long orderId, Integer supplierId, String reason);

    BigDecimal calculateRevenue(
            Integer supplierId,
            LocalDateTime start,
            LocalDateTime end
    );
    void cancelOrder(Long orderId, Integer customerId);
}
