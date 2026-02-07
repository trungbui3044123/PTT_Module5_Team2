package com.module5.team2.controllers;

import com.module5.team2.dto.request.ProductRequest;
import com.module5.team2.dto.response.ApiResponse;
import com.module5.team2.dto.response.ProductResponse;
import com.module5.team2.entity.UserEntity;
import com.module5.team2.security.jwt.CustomUserDetails;
import com.module5.team2.service.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/products")
public class ProductController {
    private final ProductService productService;

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct (
            Authentication authentication,
            @ModelAttribute @Valid ProductRequest request,
            @RequestParam("files")MultipartFile[] files
    ) throws Exception {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UserEntity currentSupplier = userDetails.getUserEntity();

        ProductResponse data = productService.addProduct(request,files,currentSupplier);

        return ResponseEntity.ok(
                ApiResponse.<ProductResponse>builder()
                        .status(200)
                        .message("Thêm sản phẩm thành công")
                        .data(data)
                        .build()
        );

    }
}
