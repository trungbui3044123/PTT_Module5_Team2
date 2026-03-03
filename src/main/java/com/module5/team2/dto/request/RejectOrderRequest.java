package com.module5.team2.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RejectOrderRequest {
    @NotBlank(message = "Lý do từ chối không được để trống")
    @Size(min = 10, max = 500, message = "Lý do không được ngắn hơn 10 ký tự hoặc vượt quá 500 ký tự")
    private String reason;
}