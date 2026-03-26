package com.module5.team2.dto.response;

import com.module5.team2.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class OrderSummaryResponse {
    private Long id;
    private OrderStatus status;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private CouponResponse coupon;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
}
