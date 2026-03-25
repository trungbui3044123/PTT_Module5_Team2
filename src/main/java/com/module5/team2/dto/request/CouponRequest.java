package com.module5.team2.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CouponRequest {
    @NotBlank(message = "Code không được để trống")
    private String code;

    @NotNull
    @Min(value = 1, message = "Giảm tối thiểu 1%")
    @Max(value = 100, message = "Không quá 100%")
    private Double value;

    @NotNull
    @Min(value = 0, message = "Min >= 0")
    private Double minOrderValue;

    @NotNull
    @Min(value = 1, message = "Số lượt >= 1")
    private Integer usageLimit;

    private LocalDateTime expiresAt;
}
