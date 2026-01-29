package com.module5.team2.dto.request;

import com.module5.team2.entity.UserEntity;
import com.module5.team2.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Username is required")
    private String username;
    @NotBlank(message = "Password is required")
    private String password;
    @NotBlank(message = "Email is required")
    private String email;
    private String phone;
    private String name;

    private Role role;
}
