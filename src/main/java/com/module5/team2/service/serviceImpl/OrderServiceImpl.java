package com.module5.team2.service.serviceImpl;

import com.module5.team2.dto.request.OrderRequest;
import com.module5.team2.dto.response.OrderItemResponse;
import com.module5.team2.dto.response.OrderResponse;
import com.module5.team2.dto.response.OrderSummaryResponse;
import com.module5.team2.entity.*;
import com.module5.team2.enums.OrderStatus;
import com.module5.team2.exception.BusinessException;
import com.module5.team2.exception.ResourceNotFoundException;
import com.module5.team2.repository.*;
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
import java.util.List;
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
    private final CouponRepository couponRepository;

    private void restoreCoupon(Order order) {
        Coupon coupon = order.getCoupon(); // bạn cần lưu coupon vào order

        if (coupon != null) {
            coupon.setUsedCount(coupon.getUsedCount() - 1);
        }
    }

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
        restoreCoupon(order);

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

        if (order.getStatus() != OrderStatus.SUCCESS) {
            throw new BusinessException("Chỉ được hủy đơn đã xác nhận");
        }

        order.setStatus(OrderStatus.CANCEL);

        restoreStock(order);
        restoreCoupon(order);

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
//public List<Order> createOrder(Integer customerId, OrderRequest request) {
public Order createOrder (Integer customerId, OrderRequest request) {

    Cart cart = cartService.getCartByCustomer(customerId);

    if (request.getCartItemIds() == null || cart.getItems().isEmpty()) {
        throw new BusinessException("Giỏ hàng trống");
    }

    UserEntity customer = userRepository.findById(customerId)
            .orElseThrow(() -> new BusinessException("Không tìm thấy khách hàng"));

    // Gom cart items theo supplier
//    Map<UserEntity, List<CartItem>> groupedBySupplier =
//            cart.getItems().stream().collect(Collectors.groupingBy(
//                    item -> item.getProduct().getSupplier()
//            ));

    // Danh sách order sẽ trả về
//    List<Order> createdOrders = new ArrayList<>();

        List<CartItem> selectedItems = cart.getItems().stream()
                .filter(item -> request.getCartItemIds().contains(item.getId()))
                .toList();

        if (selectedItems.isEmpty()) {
            throw new BusinessException("Không có sản phẩm hợp lệ");
        }

        UserEntity supplier = selectedItems.get(0).getProduct().getSupplier();

        boolean sameSupplier = selectedItems.stream()
                .allMatch(item -> item.getProduct().getSupplier().getId().equals(supplier.getId()));

        if (!sameSupplier) {
            throw new BusinessException("Chỉ được chọn sản phẩm cùng 1 nhà cung cấp");
        }

        Coupon coupon = null;

        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            coupon = couponRepository
                    .findByCodeAndIsActiveTrue(request.getCouponCode())
                    .orElseThrow(() -> new BusinessException("Mã không hợp lệ"));

            if (!coupon.getSupplier().getId().equals(supplier.getId())) {
                throw new BusinessException("Mã không thuộc shop này");
            }

            if (coupon.getExpiresAt() != null &&
                    coupon.getExpiresAt().isBefore(LocalDateTime.now())) {
                throw new BusinessException("Mã đã hết hạn");
            }

            if (coupon.getUsageLimit() != null &&
                    coupon.getUsedCount() >= coupon.getUsageLimit()) {
                throw new BusinessException("Mã đã hết lượt sử dụng");
            }
        }

        Order order = new Order();
        if (coupon != null) {
            order.setCoupon(coupon); // thêm dòng này
        }
        order.setCustomer(customer);
        order.setSupplier(supplier);
        order.setReceiverName(request.getReceiverName());
        order.setReceiverPhone(request.getReceiverPhone());
        order.setReceiverAddress(request.getReceiverAddress());
        order.setStatus(OrderStatus.PENDING);

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : selectedItems) {

            ProductEntity product = cartItem.getProduct();

            if (product.getQuantity() < cartItem.getQuantity()) {
                throw new BusinessException("Sản phẩm " + product.getName() + " không đủ số lượng");
            }

            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
            productRepository.save(product);

            BigDecimal unitPrice = BigDecimal.valueOf(product.getPrice());
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(unitPrice);
            orderItem.setSubtotal(subtotal);

            order.getItems().add(orderItem);

            total = total.add(subtotal);
        }

        if (coupon != null) {

            if (coupon.getMinOrderValue() != null &&
                    total.compareTo(BigDecimal.valueOf(coupon.getMinOrderValue())) < 0) {
                throw new BusinessException("Chưa đạt giá trị tối thiểu để dùng mã");
            }

            BigDecimal discount = total
                    .multiply(BigDecimal.valueOf(coupon.getValue()))
                    .divide(BigDecimal.valueOf(100));

            total = total.subtract(discount);

            // tăng số lần dùng
            coupon.setUsedCount(coupon.getUsedCount() + 1);
        }

        order.setTotalAmount(total);

        Order savedOrder = orderRepository.save(order);

        // ================= REMOVE ITEM ĐÃ MUA =================
        cart.getItems().removeAll(selectedItems);

        return savedOrder;



//    for (Map.Entry<UserEntity, List<CartItem>> entry : groupedBySupplier.entrySet()) {
//
//        UserEntity supplier = entry.getKey();
//        List<CartItem> supplierItems = entry.getValue();
//
//        Order order = new Order();
//        order.setCustomer(customer);
//        order.setSupplier(supplier);
//        order.setReceiverName(request.getReceiverName());
//        order.setReceiverPhone(request.getReceiverPhone());
//        order.setReceiverAddress(request.getReceiverAddress());
//        order.setStatus(OrderStatus.PENDING);
//
//        BigDecimal total = BigDecimal.ZERO;
//
//        for (CartItem cartItem : supplierItems) {
//
//            ProductEntity product = cartItem.getProduct();
//
//            if (product.getQuantity() < cartItem.getQuantity()) {
//                throw new BusinessException("Sản phẩm " + product.getName() + " không đủ số lượng");
//            }
//
//            // Trừ tồn kho
//            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
//            productRepository.save(product);
//
//            // Tạo OrderItem
//            OrderItem orderItem = new OrderItem();
//            orderItem.setOrder(order);
//            orderItem.setProduct(product);
//            orderItem.setQuantity(cartItem.getQuantity());
//
//            // Giá 1 sản phẩm
//            BigDecimal unitPrice = BigDecimal.valueOf(product.getPrice());
//            orderItem.setUnitPrice(unitPrice);
//
//            // Thành tiền
//            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
//            orderItem.setSubtotal(subtotal);
//
//            order.getItems().add(orderItem);
//
//            total = total.add(subtotal);
//        }
//
//        order.setTotalAmount(total);
//
//        Order savedOrder = orderRepository.save(order);
//        createdOrders.add(savedOrder); //  thêm vào list trả về
//
//        // Tạo thông báo cho supplier
//        notificationService.createNotification(
//                supplier,
//                "Có đơn hàng mới",
//                "Bạn có đơn hàng #" + savedOrder.getId() + " từ khách " + customer.getName(),
//                "ORDER_NEW",
//                savedOrder
//        );
//    }

    // Xóa giỏ hàng sau khi tạo đơn
//    cartService.clearCart(customerId);

//    return createdOrders; //  trả về danh sách tất cả đơn hàng


}


// end
}
