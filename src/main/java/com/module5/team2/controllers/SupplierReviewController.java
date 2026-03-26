package com.module5.team2.controllers;

import com.module5.team2.dto.request.SupplierResponseReviewRequest;
import com.module5.team2.dto.response.ApiResponse;
import com.module5.team2.entity.ReviewEntity;
import com.module5.team2.service.service.ProductReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/supplier/reviews")
public class SupplierReviewController {

    private final ProductReviewService reviewService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReviewEntity>>> getReviews(
            @RequestParam Integer supplierId
    ) {
        return ResponseEntity.ok(
                ApiResponse.<List<ReviewEntity>>builder()
                        .status(200)
                        .message("Lấy danh sách review thành công")
                        .data(reviewService.getReviewsBySupplier(supplierId))
                        .build()
        );
    }

    @PutMapping("/{reviewId}/response")
    public ResponseEntity<ApiResponse<String>> respondReview(
            @PathVariable Long reviewId,
            @RequestParam Integer supplierId,
            @RequestBody @Valid SupplierResponseReviewRequest request
    ) {

        reviewService.respondReview(reviewId, supplierId, request.getResponse());

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .status(200)
                        .message("Phản hồi đánh giá thành công")
                        .data(null)
                        .build()
        );
    }
}
