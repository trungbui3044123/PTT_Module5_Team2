package com.module5.team2.dto.request;

import lombok.Data;

@Data
public class CreateStaffRequest {
    private String username;
    private String password;
    private String email;
    private String phone;
    private String name;
    private Double salary;
}
