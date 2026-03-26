package com.module5.team2.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SupplierResponseReviewRequest {
    @NotBlank(message = "Nội dung phản hồi không được để trống")
    private String response;
}
