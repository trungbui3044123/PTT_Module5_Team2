package com.module5.team2.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileResponse {
    private Integer id;
    private String username;
    private String email;
    private String phone;
    private String name;
    private Integer age;
    private String address;
    private String role;
    private String status;
    private Double salary; // chỉ meaningful với STAFF
}
