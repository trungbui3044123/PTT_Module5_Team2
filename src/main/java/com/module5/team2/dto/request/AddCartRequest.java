package com.module5.team2.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddCartRequest {
    private Integer customerId;
    private Integer productId;
    private Integer quantity;
}
