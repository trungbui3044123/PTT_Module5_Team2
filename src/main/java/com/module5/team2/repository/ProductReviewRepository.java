package com.module5.team2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.module5.team2.entity.ReviewEntity;

public interface ProductReviewRepository extends JpaRepository<ReviewEntity, Long>{
 // Lấy danh sách review theo sản phẩm
    List<ReviewEntity> findByProductId(Integer productId);

    // Kiểm tra user đã đánh giá sản phẩm chưa
    boolean existsByProductIdAndUserId(Integer productId, Integer userId);
}
