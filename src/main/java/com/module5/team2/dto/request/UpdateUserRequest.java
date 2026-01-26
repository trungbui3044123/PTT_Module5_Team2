package com.module5.team2.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * UpdateUserRequest
 * -----------------
 * Dùng cho:
 * - Nhân sự cập nhật thông tin cá nhân
 * - Admin update user
 */
@Data
public class UpdateUserRequest {

    @NotBlank(message = "Tên không được để trống")
    private String name;

    @NotNull(message = "Tuổi không được để trống")
    @Min(value = 19, message = "Tuổi phải > 18")
    @Max(value = 59, message = "Tuổi phải < 60")
    private Integer age;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(
            regexp = "^(0|\\+84)[0-9]{9}$",
            message = "Số điện thoại không hợp lệ"
    )
    private String phone;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;
}
