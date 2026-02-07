package com.module5.team2.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProductResponse {
    private Integer id;
    private String name;
    private Double price;
    private String status;
    private List<String> imageUrls;
}
