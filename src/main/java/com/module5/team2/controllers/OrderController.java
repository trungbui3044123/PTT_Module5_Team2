package com.module5.team2.controllers;

import java.util.List;
import java.util.stream.Collectors;

import com.module5.team2.dto.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.module5.team2.dto.request.OrderRequest;
import com.module5.team2.dto.response.ApiResponse;
import com.module5.team2.dto.response.OrderItemResponse;
import com.module5.team2.dto.response.OrderResponse;
import com.module5.team2.dto.response.ProductResponse;
import com.module5.team2.entity.Order;
import com.module5.team2.exception.BusinessException;
import com.module5.team2.security.jwt.CustomUserDetails;
import com.module5.team2.service.service.OrderService;

import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/public/order")
@RequiredArgsConstructor
public class OrderController {

        private final OrderService orderService;

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderDetail(
            @PathVariable Long orderId,
            Authentication authentication
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // Lấy order
        Order order = orderService.getOrder(orderId);

        // Check quyền
        if (!order.getCustomer().getId().equals(userDetails.getId())) {
            throw new BusinessException("Bạn không có quyền xem đơn hàng này");
        }

        return ResponseEntity.ok(toResponse(order));
    }

        @GetMapping("/customer/{id}")
        public ResponseEntity<ApiResponse<Page<OrderResponse>>> getMethodName(
                        Authentication authentication,
                        @PathVariable("id") int userId,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

                // Không cho xem order của người khác
                if (userDetails.getId() != userId) {
                        throw new BusinessException("Bạn không có quyền xem đơn hàng của người dùng này");
                }
                Pageable pageable = PageRequest.of(page,
                                size,
                                Sort.by("id").descending());
                Page<OrderResponse> response = orderService.findByCustomer(userId, pageable);

                return ResponseEntity.ok(
                                ApiResponse.<Page<OrderResponse>>builder()
                                                .status(200)
                                                .message("Lấy danh sách thành công")
                                                .data(response)
                                                .build());

        }

        @PutMapping("/cancel")
         public ResponseEntity<Void> cancelOrder(Authentication authentication,
                             @RequestParam("orderId") Long orderId){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
         if (userDetails==null) {
                        throw new BusinessException("Bạn không có quyền huy đơn hàng của người dùng này");
                }
          orderService.cancelOrder(orderId, userDetails.getId());     
          return ResponseEntity.noContent().build();
         }


    @PostMapping("/create")
    public ResponseEntity<OrderResponse> createOrder(
            @RequestParam Integer customerId,
            @RequestBody OrderRequest request
    ) {

        Order orders =  orderService.createOrder(customerId, request);

        // Convert sang DTO để tránh trả về entity thô
         OrderResponse response = toResponse(orders);                                     

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
                                .coupon(order.getCoupon() != null ? CouponResponse.builder()
                                        .id(order.getCoupon().getId())
                                        .code(order.getCoupon().getCode())
                                        .value(order.getCoupon().getValue())
                                        .build() : null)
                                .items(
                                                order.getItems().stream()
                                                                .map(item -> OrderItemResponse.builder()
                                                                                .productId(item.getProduct().getId())
                                                                                .productName(item.getProduct().getName())
                                                                                .quantity(item.getQuantity())
                                                                                .unitPrice(item.getUnitPrice())
                                                                                .subtotal(item.getSubtotal())
                                                                                .productImageUrl(item.getProduct().getImages().getFirst().getImageUrl())
                                                                                .build())
                                                                .collect(Collectors.toList()))
                                .build();
        }

        // end
}
