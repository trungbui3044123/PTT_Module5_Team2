package com.module5.team2.service.serviceImpl;

import com.module5.team2.dto.request.OrderRequest;
import com.module5.team2.dto.response.OrderItemResponse;
import com.module5.team2.dto.response.OrderResponse;
import com.module5.team2.dto.response.OrderSummaryResponse;
import com.module5.team2.entity.Cart;
import com.module5.team2.entity.CartItem;
import com.module5.team2.entity.Order;
import com.module5.team2.entity.OrderItem;
import com.module5.team2.entity.ProductEntity;
import com.module5.team2.entity.UserEntity;
import com.module5.team2.enums.OrderStatus;
import com.module5.team2.exception.BusinessException;
import com.module5.team2.exception.ResourceNotFoundException;
import com.module5.team2.repository.OrderRepository;
import com.module5.team2.repository.ProductRepository;
import com.module5.team2.repository.UserRepository;
import com.module5.team2.service.service.CartService;
import com.module5.team2.service.service.NotificationService;
import com.module5.team2.service.service.OrderService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final NotificationService notificationService;
    private final CartService cartService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

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
                                .productId(Integer.valueOf(item.getProduct().getId()))
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
                "ORDER_SUCCESS",
                order
        );
    }

    private void restoreStock(Order order) {
        for (OrderItem item : order.getItems()) {
            ProductEntity product = item.getProduct();

            product.setQuantity(
                    product.getQuantity() + item.getQuantity()
            );
        }
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

        restoreStock(order);

        order.setStatus(OrderStatus.REJECT);
        order.setRejectReason(reason);

        notificationService.createNotification(
                order.getCustomer(),
                "Đơn hàng bị từ chối",
                "Đơn hàng #" + order.getId() + " bị từ chối. Lý do: " + reason,
                "ORDER_REJECT",
                order
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

    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Không tìm thấy đơn hàng"));
    }

    @Override
    public void cancelOrder(Long orderId, Integer customerId) {

        Order order = getOrder(orderId);

        if (!order.getCustomer().getId().equals(customerId)) {
            throw new BusinessException("Bạn không có quyền hủy đơn này");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Chỉ được hủy đơn đang chờ");
        }

        order.setStatus(OrderStatus.CANCEL);

        restoreStock(order);

        notificationService.createNotification(
                order.getSupplier(),
                "Khách hàng đã hủy đơn",
                "Khách hàng đã hủy đơn hàng #" + order.getId(),
                "ORDER_CANCEL",
                order
        );
    }

    @Override
@Transactional
public List<Order> createOrder(Integer customerId, OrderRequest request) {

    Cart cart = cartService.getCartByCustomer(customerId);

    if (cart.getItems().isEmpty()) {
        throw new BusinessException("Giỏ hàng trống");
    }

    UserEntity customer = userRepository.findById(customerId)
            .orElseThrow(() -> new BusinessException("Không tìm thấy khách hàng"));

    // Gom cart items theo supplier
    Map<UserEntity, List<CartItem>> groupedBySupplier =
            cart.getItems().stream().collect(Collectors.groupingBy(
                    item -> item.getProduct().getSupplier()
            ));

    // Danh sách order sẽ trả về
    List<Order> createdOrders = new ArrayList<>();

    for (Map.Entry<UserEntity, List<CartItem>> entry : groupedBySupplier.entrySet()) {

        UserEntity supplier = entry.getKey();
        List<CartItem> supplierItems = entry.getValue();

        Order order = new Order();
        order.setCustomer(customer);
        order.setSupplier(supplier);
        order.setReceiverName(request.getReceiverName());
        order.setReceiverPhone(request.getReceiverPhone());
        order.setReceiverAddress(request.getReceiverAddress());
        order.setStatus(OrderStatus.PENDING);

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : supplierItems) {

            ProductEntity product = cartItem.getProduct();

            if (product.getQuantity() < cartItem.getQuantity()) {
                throw new BusinessException("Sản phẩm " + product.getName() + " không đủ số lượng");
            }

            // Trừ tồn kho
            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
            productRepository.save(product);

            // Tạo OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());

            // Giá 1 sản phẩm
            BigDecimal unitPrice = BigDecimal.valueOf(product.getPrice());
            orderItem.setUnitPrice(unitPrice);

            // Thành tiền
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            orderItem.setSubtotal(subtotal);

            order.getItems().add(orderItem);

            total = total.add(subtotal);
        }

        order.setTotalAmount(total);

        Order savedOrder = orderRepository.save(order);
        createdOrders.add(savedOrder); //  thêm vào list trả về

        // Tạo thông báo cho supplier
        notificationService.createNotification(
                supplier,
                "Có đơn hàng mới",
                "Bạn có đơn hàng #" + savedOrder.getId() + " từ khách " + customer.getName(),
                "ORDER_NEW",
                savedOrder
        );
    }

    // Xóa giỏ hàng sau khi tạo đơn
    cartService.clearCart(customerId);

    return createdOrders; //  trả về danh sách tất cả đơn hàng
}

    @Override
    public Page<OrderResponse> findByCustomer(Integer customerId, Pageable pageable) throws NullPointerException{
         UserEntity customer = userRepository.findById(customerId)
            .orElseThrow(() -> new BusinessException("Không tìm thấy khách hàng"));

        Page<Order> response= orderRepository.findByCustomer(customer, pageable);
       
        return response.map(order->
            OrderResponse.builder()
                                .id(order.getId())
                                .status(order.getStatus())
                                .supplierName(order.getSupplier().getName())
                                .receiverName(order.getReceiverName())
                                .receiverPhone(order.getReceiverPhone())
                                .receiverAddress(order.getReceiverAddress())
                                .totalAmount(order.getTotalAmount())
                                .rejectReason(order.getRejectReason())
                                .createdAt(order.getCreatedAt())
                                .items(
                                    order.getItems().stream()
                                        .map(item->OrderItemResponse.builder()
                                        .productId(item.getProduct().getId())
                                        .productName(item.getProduct().getName())
                                        .quantity(item.getQuantity())
                                        .unitPrice(item.getUnitPrice())
                                        .subtotal(item.getSubtotal())
                                        .build()
                            )
                            .toList()
                        )
                                .build()
                            );
    }


// end
}
