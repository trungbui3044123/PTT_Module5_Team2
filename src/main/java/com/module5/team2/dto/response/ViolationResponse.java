package com.module5.team2.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ViolationResponse {

    private Long id;

    private String customerName;

    private String supplierName;

    private String staffName;

    private String violation;

    private String status;

    private LocalDateTime createdAt;

    private String customerEmail;

    private String supplierEmail;
}