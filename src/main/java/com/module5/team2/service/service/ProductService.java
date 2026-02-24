package com.module5.team2.service.service;

import com.module5.team2.dto.request.ProductRequest;
import com.module5.team2.dto.response.ProductResponse;
import com.module5.team2.entity.UserEntity;
import com.module5.team2.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {
    // read

    Page<ProductResponse> getMyProducts(UserEntity supplier,
                                        String keyword,
                                        Pageable pageable);
    Page<ProductResponse> getProducts(String keyword,Pageable pageable);
    Page<ProductResponse> getByCateogries(String keyword,Pageable pageable);
    //save
    ProductResponse addProduct(ProductRequest request, MultipartFile[] files, UserEntity supplier) throws IOException;


    ProductResponse getProductDetail(Integer id);

    ProductResponse updateProduct(Integer productId, ProductRequest request, MultipartFile[] files, ProductStatus status) throws IOException;
}
