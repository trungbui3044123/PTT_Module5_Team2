package com.module5.team2.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.module5.team2.entity.ReviewEntity;
import org.springframework.data.jpa.repository.Query;

public interface ProductReviewRepository extends JpaRepository<ReviewEntity, Long>{
 // Lấy danh sách review theo sản phẩm
    List<ReviewEntity> findByProductId(Integer productId);

    // Kiểm tra user đã đánh giá sản phẩm chưa
    boolean existsByProductIdAndUserId(Integer productId, Integer userId);

    @Query("""
    SELECT r FROM ReviewEntity r
    WHERE r.id = :reviewId
    AND r.product.supplier.id = :supplierId
""")
    Optional<ReviewEntity> findByIdAndSupplierId(Long reviewId, Integer supplierId);

   @Query("""
    SELECT r FROM ReviewEntity r
    WHERE r.product.supplier.id = :supplierId
""")
   List<ReviewEntity> findBySupplierId(Integer supplierId);
}
