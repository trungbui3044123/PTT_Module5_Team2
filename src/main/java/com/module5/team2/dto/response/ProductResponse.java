package com.module5.team2.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

import com.module5.team2.entity.UserEntity;

@Data
@Builder
public class ProductResponse {
    private Integer id;
    private String name;
    private String category;
    private Double price;
    private Integer quantity;
    private String description;
    private String status;
    private List<String> imageUrls;
    private String supplierName;
}
