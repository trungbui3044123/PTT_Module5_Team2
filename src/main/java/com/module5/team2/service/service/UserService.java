package com.module5.team2.service.service;

import com.module5.team2.dto.request.CreateStaffRequest;
import com.module5.team2.dto.request.RegisterRequest;
import com.module5.team2.entity.UserEntity;

import java.util.List;

public interface UserService {
    UserEntity register(RegisterRequest request);

    UserEntity createStaff(CreateStaffRequest request);

    void resetStaffPassword(Integer staffId);

    UserEntity findByUsername(String username);

    List<UserEntity> searchUsers(String keyword);
}
