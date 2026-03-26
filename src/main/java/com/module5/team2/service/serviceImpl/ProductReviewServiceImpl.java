package com.module5.team2.service.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.module5.team2.dto.request.ReviewRequest;
import com.module5.team2.entity.ProductEntity;
import com.module5.team2.entity.ReviewEntity;
import com.module5.team2.entity.UserEntity;
import com.module5.team2.exception.BusinessException;
import com.module5.team2.repository.ProductRepository;
import com.module5.team2.repository.ProductReviewRepository;
import com.module5.team2.repository.UserRepository;
import com.module5.team2.service.service.NotificationService;
import com.module5.team2.service.service.ProductReviewService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class ProductReviewServiceImpl implements ProductReviewService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductReviewRepository reviewRepository;
    private final NotificationService notifiService;

    @Override
    public List<ReviewEntity> getReviewsByProduct(Integer productId) {
        return reviewRepository.findByProductId(productId);
    }

    @SuppressWarnings("null")
    @Override
    public void createReview(Integer productId, Integer userId, ReviewRequest request) throws NullPointerException {
        if (reviewRepository.existsByProductIdAndUserId(productId, userId)) {
            throw new BusinessException("Bạn đã đánh giá sản phẩm này rồi");
        }
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy sản phẩm"));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy người dùng"));
        UserEntity supplier = userRepository.findById(productRepository.findSupplierIdByProductId(productId))
                .orElseThrow(() -> new BusinessException("Không tìm thấy người dùng"));
        ReviewEntity review = ReviewEntity.builder()
                .rating(request.getRating())
                .comment(request.getComment())
                .createdAt(LocalDateTime.now())
                .user(user)
                .product(product)
                .build();
        reviewRepository.save(review);
        notifiService.createReviewNotify(
                supplier,
                review.getComment(),
                " Khách hàng "+user.getName() + " đã đánh giá "+review.getRating()+" sao cho sản phẩm :" + productId,
                "REVIEW_PRODUCT",
                review);
        
    }

    @Override
    public List<ReviewEntity> getReviewsBySupplier(Integer supplierId) {
        return reviewRepository.findBySupplierId(supplierId);
    }

    @Override
    public void respondReview(Long reviewId, Integer supplierId, String response) {

        ReviewEntity review = reviewRepository
                .findByIdAndSupplierId(reviewId, supplierId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy review hoặc không có quyền"));

        if (review.getSupplierResponse() != null &&
                !review.getSupplierResponse().trim().isEmpty()) {
            throw new BusinessException("Review này đã được phản hồi rồi");
        }

        review.setSupplierResponse(response);
        reviewRepository.save(review);

        // Optional: gửi thông báo cho user
        notifiService.createReviewNotify(
                review.getUser(),
                response,
                "Shop đã phản hồi đánh giá của bạn",
                "REPLY_REVIEW",
                review
        );
    }

}
