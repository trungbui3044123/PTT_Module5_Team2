package com.module5.team2.service.service;

import com.module5.team2.dto.request.ChangePasswordRequest;
import com.module5.team2.dto.request.CreateStaffRequest;
import com.module5.team2.dto.request.RegisterRequest;
import com.module5.team2.dto.request.UpdateUserRequest;
import com.module5.team2.entity.UserEntity;
import com.module5.team2.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface UserService {
    UserEntity updateUser(Integer userId, UpdateUserRequest request);

    void changeStatus(Integer userId, Status status);

    void deleteUser(Integer userId);

    void changePassword(Integer userId, ChangePasswordRequest request);

    void forgotPassword(String email);

  
    void resetStaffPassword(Integer staffId);

    UserEntity register(RegisterRequest request);

    UserEntity createStaff(CreateStaffRequest request);

    UserEntity findByUsername(String username);

//    List<UserEntity> searchUsers(String keyword);

    Page<UserEntity> searchUsers(String keyword, Pageable pageable);
}
