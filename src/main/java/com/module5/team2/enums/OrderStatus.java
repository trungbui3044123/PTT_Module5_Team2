package com.module5.team2.enums;

public enum OrderStatus {
    PENDING, // Đang chờ xác nhận (kể cả đang chờ giao) 
    REJECT, // Từ chối từ phía cửa hàng 
    CANCEL, // Hủy đơn từ người mua
    SUCCESS // Thanh toán/hoàn tất
}
