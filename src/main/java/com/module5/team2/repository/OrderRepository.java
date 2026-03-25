package com.module5.team2.repository;

import com.module5.team2.entity.Order;
import com.module5.team2.entity.UserEntity;
import com.module5.team2.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
//    Long findById(Long orderId);
    Page<Order> findBySupplierId(Integer supplier_id, Pageable pageable);
    Page<Order> findBySupplierIdAndStatus(
            Integer supplierId,
            OrderStatus status,
            Pageable pageable
    );
    List<Order> findBySupplierIdAndStatusAndCreatedAtBetween(
            Integer supplierId,
            OrderStatus status,
            LocalDateTime start,
            LocalDateTime end
    );
    Page<Order> findByCustomer(UserEntity customer, Pageable pageable);
}
