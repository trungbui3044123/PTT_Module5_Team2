package com.module5.team2.repository;

import com.module5.team2.dto.response.ShopRevenueResponse;
import com.module5.team2.entity.Order;
import com.module5.team2.entity.UserEntity;
import com.module5.team2.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
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
    @Query("""
SELECT 
    u.id,
    u.name,
    COALESCE(SUM(o.totalAmount), 0.0)
FROM Order o
JOIN o.supplier u
WHERE o.status = com.module5.team2.enums.OrderStatus.SUCCESS
GROUP BY u.id, u.name
""")
    List<Object[]> getShopRevenueRaw();

    @Query("""
SELECT COALESCE(SUM(o.totalAmount), 0)
FROM Order o
WHERE o.supplier.id = :supplierId
AND o.status = com.module5.team2.enums.OrderStatus.SUCCESS
""")
    BigDecimal getRevenueBySupplier(Integer supplierId);
    Page<Order> findByCustomer(UserEntity customer, Pageable pageable);
}
