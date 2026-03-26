package com.module5.team2.service.service;

import java.util.List;

import com.module5.team2.dto.request.ReviewRequest;
import com.module5.team2.entity.ReviewEntity;

public interface ProductReviewService {
    List<ReviewEntity> getReviewsByProduct(Integer productId);
    void createReview(Integer productId, Integer userId, ReviewRequest request);

    List<ReviewEntity> getReviewsBySupplier(Integer supplierId);
    void respondReview(Long reviewId, Integer supplierId, String response);
}
