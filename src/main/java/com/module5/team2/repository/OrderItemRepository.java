package com.module5.team2.repository;

import com.module5.team2.entity.OrderItem;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    @Query(value = """
    SELECT 
        oi.product_id,
        p.name AS product_name,
        p.category AS product_category,
        p.price AS product_price,
        p.quantity AS product_quantity,
        p.description AS product_description,
        p.status AS product_status,
        pi.image_url AS product_imageUrls,
        u.name AS product_supplierName
    FROM order_items oi
    JOIN orders o ON oi.order_id = o.id
    JOIN products p ON oi.product_id = p.id
    LEFT JOIN product_images pi ON pi.product_id = p.id
    JOIN users u ON o.supplier_id = u.id
    WHERE o.supplier_id = :supplierId
    GROUP BY 
        oi.product_id,
        p.name,
        p.category,
        p.price,
        p.quantity,
        p.description,
        p.status,
        pi.image_url,
        u.name
    ORDER BY p.price DESC
    LIMIT 5
""", nativeQuery = true)
List<Object[]> findTop5BestSellingProducts(@Param("supplierId") Long supplierId);

//    boolean existsByCouponCode(String couponCode);
}
