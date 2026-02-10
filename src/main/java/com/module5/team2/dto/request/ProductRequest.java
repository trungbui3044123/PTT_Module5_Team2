package com.module5.team2.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProductRequest {

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(min = 2, max = 100, message = "Tên sản phẩm phải từ 2 đến 100 ký tự")
    private String name;

    @NotBlank(message = "Mô tả không được để trống")
    @Size(max = 5000, message = "Mô tả không được vượt quá 5000 ký tự")
    // DONE: Gioi han kich thuoc content (vi du: 5000 length)
    private String description;

    @NotNull(message = "Giá không được để trống")
    @DecimalMin(value = "1000.0", message = "Giá sản phẩm phải từ 1,000 VND trở lên")
    private Double price;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng phải ít nhất là 1")
    private Integer quantity;

    @NotBlank(message = "Loại sản phẩm không được để trống")
    private String category;
}
