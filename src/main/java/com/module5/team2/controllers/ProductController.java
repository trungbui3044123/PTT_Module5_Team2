package com.module5.team2.controllers;

import com.module5.team2.dto.request.ProductRequest;
import com.module5.team2.dto.response.ApiResponse;
import com.module5.team2.dto.response.ProductResponse;
import com.module5.team2.entity.UserEntity;
import com.module5.team2.enums.ProductStatus;
import com.module5.team2.security.jwt.CustomUserDetails;
import com.module5.team2.service.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
//read
    @GetMapping("/my-products")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getMyProducts(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword
    ) {

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("id").descending()
        );

        Page<ProductResponse> data = productService.getMyProducts(
                userDetails.getUserEntity(),
                keyword,
                pageable
        );

        return ResponseEntity.ok(
                ApiResponse.<Page<ProductResponse>>builder()
                        .status(200)
                        .message("Lấy danh sách thành công")
                        .data(data)
                        .build()
        );
    }
    @GetMapping("/allusers")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword
    ) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("id").descending()
        );

        Page<ProductResponse> data = productService.getProducts(
                keyword,
                pageable
        );

        return ResponseEntity.ok(
                ApiResponse.<Page<ProductResponse>>builder()
                        .status(200)
                        .message("Lấy danh sách thành công")
                        .data(data)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductDetail(@PathVariable Integer id) {
        ProductResponse data = productService.getProductDetail(id);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy thông tin thành công", data));
    }

    @GetMapping("/allusers/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getAllProductDetail(@PathVariable Integer id) {
        
        ProductResponse data = productService.getProductDetail(id);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy thông tin thành công", data));
    }
    @GetMapping("/allusers/categories")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getByCateogries(
         @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam  String category) {
        Pageable pageable = PageRequest.of(
        page,
        size
);
        Page<ProductResponse> data = productService.getByCateogries(category,pageable);
         return ResponseEntity.ok(
                ApiResponse.<Page<ProductResponse>>builder()
                        .status(200)
                        .message("Lấy danh sách thành công")
                        .data(data)
                        .build()
        );
    }
//edit
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Integer id,
            @ModelAttribute @Valid ProductRequest request,
            @RequestParam(value = "files", required = false) MultipartFile[] files,
            @RequestParam(value = "status", required = false) String status
    ) throws IOException {

        ProductStatus productStatus = (status != null) ? ProductStatus.valueOf(status) : null;
        ProductResponse data = productService.updateProduct(id, request, files, productStatus);

        return ResponseEntity.ok(new ApiResponse<>(200, "Cập nhật sản phẩm thành công", data));
    }
}
