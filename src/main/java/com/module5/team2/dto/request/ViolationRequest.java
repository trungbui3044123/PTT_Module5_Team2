package com.module5.team2.dto.request;

import com.module5.team2.enums.ViolationStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ViolationRequest {

    // Có thể null nhưng nếu có thì phải > 0
    @Positive(message = "User ID phải > 0")
    private Integer userId;

    // Supplier bắt buộc
    @NotNull(message = "Supplier ID không được để trống")
    @Positive(message = "Supplier ID phải > 0")
    private Integer supplierId;

    // Nội dung vi phạm
    @NotBlank(message = "Nội dung vi phạm không được để trống")
    @Size(max = 500, message = "Nội dung không được vượt quá 500 ký tự")
    private String violation;

    // Trạng thái
    @NotNull(message = "Status không được để trống")
    private ViolationStatus status;

    // Thời gian tạo (có thể bỏ nếu backend tự set)
    @PastOrPresent(message = "Ngày tạo không hợp lệ")
    private LocalDateTime createdAt;
}
