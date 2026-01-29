package com.module5.team2.dto.request;

import com.module5.team2.entity.UserEntity;
import com.module5.team2.enums.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String phone;
    private String name;

    private Role role;
}
