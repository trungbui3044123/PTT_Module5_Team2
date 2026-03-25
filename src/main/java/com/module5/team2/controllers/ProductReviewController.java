package com.module5.team2.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.module5.team2.dto.request.ProductRequest;
import com.module5.team2.dto.request.ReviewRequest;
import com.module5.team2.dto.response.ApiResponse;
import com.module5.team2.dto.response.ProductResponse;
import com.module5.team2.entity.ReviewEntity;
import com.module5.team2.service.service.ProductReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/public/reviews")
@RequiredArgsConstructor
public class ProductReviewController {
    private final ProductReviewService reviewService;
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<List<ReviewEntity>>> getReviews(@PathVariable Integer productId) {
        List<ReviewEntity> data= reviewService.getReviewsByProduct(productId);
        return ResponseEntity.ok(
                ApiResponse.<List<ReviewEntity>>builder()
                        .status(200)
                        .message("Lấy danh sách review sản phẩm thành công")
                        .data(data)
                        .build()
        );
    }

    @PostMapping("/{productId}")
    public ResponseEntity<Map<String, Object>> createReview(
            @PathVariable Integer productId,
            @RequestParam Integer userId,
            @RequestBody  @Valid ReviewRequest request
    ) {
         reviewService.createReview(productId, userId, request);
        Map<String, Object> response = new HashMap<>();
    response.put("status", 200);
    response.put("message", "Đánh giá thành công");

    return ResponseEntity.ok(response);
    }
}
