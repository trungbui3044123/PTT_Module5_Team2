package com.module5.team2.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.module5.team2.dto.request.OrderRequest;
import com.module5.team2.dto.response.OrderItemResponse;
import com.module5.team2.dto.response.OrderResponse;
import com.module5.team2.entity.Order;
import com.module5.team2.service.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/public/order")
@RequiredArgsConstructor
public class OrderController {
   
    private final OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<List<OrderResponse>> createOrder(
            @RequestParam Integer customerId,
            @RequestBody OrderRequest request
    ) {

        List<Order> orders = orderService.createOrder(customerId, request);

        // Convert sang DTO để tránh trả về entity thô
        List<OrderResponse> response = orders.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    private OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .status(order.getStatus())
                .receiverName(order.getReceiverName())
                .receiverPhone(order.getReceiverPhone())
                .receiverAddress(order.getReceiverAddress())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .items(
                        order.getItems().stream()
                                .map(item -> OrderItemResponse.builder()
                                        .productId(Long.valueOf(item.getProduct().getId()))
                                        .productName(item.getProduct().getName())
                                        .quantity(item.getQuantity())
                                        .unitPrice(item.getUnitPrice())
                                        .subtotal(item.getSubtotal())
                                        .build()
                                ).collect(Collectors.toList())
                )
                .build();
    }
}
