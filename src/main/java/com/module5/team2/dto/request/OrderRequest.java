package com.module5.team2.dto.request;

import lombok.Data;


@Data

public class OrderRequest {
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
}
