package com.module5.team2.dto.request;

import com.module5.team2.enums.Status;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;

import lombok.Getter;
import lombok.NoArgsConstructor;

import lombok.Setter;

/**
 * UpdateUserRequest
 * -----------------
 * Dùng cho:
 * - Nhân sự cập nhật thông tin cá nhân
 * - Admin update user
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    @NotBlank(message = "Tên không được để trống")
    private String username;

     @Column(nullable = false, unique = true, length = 150)
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(
            regexp = "^(0|\\+84)[0-9]{9}$",
            message = "Số điện thoại không hợp lệ"
    )
    private String phone;

    @NotNull(message = "Tuổi không được để trống")
    @Min(value = 19, message = "Tuổi phải > 18")
    @Max(value = 59, message = "Tuổi phải < 60")
    private Integer age;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;

    @NotBlank(message = "Tên không được để trống")
    private String name;


     @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;
 
   @Min(value = 1000, message = "Lương phải lớn hơn 1000")
@Digits(integer = 8, fraction = 0, message = "Lương không hợp lệ")
private Double salary;


}
