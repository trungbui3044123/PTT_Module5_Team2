package com.module5.team2.dto.request;

import com.module5.team2.enums.ViolationStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ViolationRequest {
    private Integer userId;      // customer_id (nullable)
    private Integer supplierId;
    private String violation;
    private ViolationStatus status;
    private LocalDateTime createdAt;
}
