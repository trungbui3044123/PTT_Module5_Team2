package com.module5.team2.dto.response;

import com.module5.team2.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private OrderStatus status;
    private String supplierName;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;

    private BigDecimal totalAmount;

    private String rejectReason;

    private LocalDateTime createdAt;

    private List<OrderItemResponse> items;
}
