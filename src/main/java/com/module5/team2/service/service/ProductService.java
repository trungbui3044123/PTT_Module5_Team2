package com.module5.team2.service.service;

import com.module5.team2.dto.request.ProductRequest;
import com.module5.team2.dto.response.ProductResponse;
import com.module5.team2.entity.UserEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {
    ProductResponse addProduct(ProductRequest request, MultipartFile[] files, UserEntity supplier) throws IOException;
}
