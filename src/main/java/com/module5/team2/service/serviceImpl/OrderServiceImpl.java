package com.module5.team2.service.serviceImpl;

import com.module5.team2.dto.response.OrderItemResponse;
import com.module5.team2.dto.response.OrderResponse;
import com.module5.team2.dto.response.OrderSummaryResponse;
import com.module5.team2.entity.Order;
import com.module5.team2.enums.OrderStatus;
import com.module5.team2.exception.BusinessException;
import com.module5.team2.exception.ResourceNotFoundException;
import com.module5.team2.repository.OrderRepository;
import com.module5.team2.service.service.NotificationService;
import com.module5.team2.service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final NotificationService notificationService;

    @Override
    public Page<OrderSummaryResponse> getSupplierOrders(
            Integer supplierId,
            OrderStatus status,
            Pageable pageable
    ) {

        Page<Order> orders = (status != null)
                ? orderRepository.findBySupplierIdAndStatus(supplierId, status, pageable)
                : orderRepository.findBySupplierId(supplierId, pageable);

        return orders.map(order ->
                OrderSummaryResponse.builder()
                        .id(order.getId())
                        .status(order.getStatus())
                        .receiverName(order.getReceiverName())
                        .receiverPhone(order.getReceiverPhone())
                        .receiverAddress(order.getReceiverAddress())
                        .totalAmount(order.getTotalAmount())
                        .createdAt(order.getCreatedAt())
                        .build()
        );
    }

    @Override
    public OrderResponse getOrderDetail(Long orderId, Integer supplierId) {

        Order order = getOrder(orderId);

        if (!order.getSupplier().getId().equals(supplierId)) {
            throw new BusinessException("Bạn không có quyền xem đơn hàng này");
        }

        return OrderResponse.builder()
                .id(order.getId())
                .status(order.getStatus())
                .receiverName(order.getReceiverName())
                .receiverPhone(order.getReceiverPhone())
                .receiverAddress(order.getReceiverAddress())
                .totalAmount(order.getTotalAmount())
                .rejectReason(order.getRejectReason())
                .createdAt(order.getCreatedAt())
                .items(order.getItems().stream()
                        .map(item -> OrderItemResponse.builder()
                                .productId(Long.valueOf(item.getProduct().getId()))
                                .productName(item.getProduct().getName())
                                .quantity(item.getQuantity())
                                .unitPrice(item.getUnitPrice())
                                .subtotal(item.getSubtotal())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public void confirmOrder(Long orderId, Integer supplierId) {

        Order order = getOrder(orderId);

        if (!order.getSupplier().getId().equals(supplierId)) {
            throw new BusinessException("Bạn không có quyền xác nhận đơn này");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Chỉ được xác nhận đơn đang chờ xử lý");
        }

        order.setStatus(OrderStatus.SUCCESS);
        order.setRejectReason(null);

        notificationService.createNotification(
                order.getCustomer(),
                "Đơn hàng được xác nhận",
                "Đơn hàng #" + order.getId() + " đã được xác nhận",
                "ORDER_SUCCESS"
        );
    }

    @Override
    public void rejectOrder(Long orderId, Integer supplierId, String reason) {

        Order order = getOrder(orderId);

        if (!order.getSupplier().getId().equals(supplierId)) {
            throw new BusinessException("Bạn không có quyền từ chối đơn này");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Chỉ được từ chối đơn đang chờ xử lý");
        }

        order.setStatus(OrderStatus.REJECT);
        order.setRejectReason(reason);

        notificationService.createNotification(
                order.getCustomer(),
                "Đơn hàng bị từ chối",
                "Đơn hàng #" + order.getId() + " bị từ chối. Lý do: " + reason,
                "ORDER_REJECT"
        );
    }

    @Override
    public BigDecimal calculateRevenue(
            Integer supplierId,
            LocalDateTime start,
            LocalDateTime end
    ) {

        if (start.isAfter(end)) {
            throw new BusinessException("Thời gian bắt đầu phải nhỏ hơn thời gian kết thúc");
        }

        return orderRepository
                .findBySupplierIdAndStatusAndCreatedAtBetween(
                        supplierId,
                        OrderStatus.SUCCESS,
                        start,
                        end
                )
                .stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Không tìm thấy đơn hàng"));
    }
}
