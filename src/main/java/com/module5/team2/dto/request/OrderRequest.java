package com.module5.team2.dto.request;

import lombok.Data;

import java.util.List;


@Data

public class OrderRequest {
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;

    private List<Long> cartItemIds;
    private String couponCode;
}
