package com.module5.team2.dto.request;

import lombok.Data;

@Data
public class CreateStaffRequest {
    private String username;
    private String email;
    private String phone;
    private Integer age;
    private String name;
    private String address;
    private Double salary;
}
