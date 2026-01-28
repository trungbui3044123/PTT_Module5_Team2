package com.module5.team2.service.service;

import com.module5.team2.dto.request.UpdateUserRequest;
import com.module5.team2.entity.UserEntity;


public interface UserService {
    UserEntity updateUser(Integer userId, UpdateUserRequest request);

    void changeStatus(Integer userId, UserEntity.Status status);

    void deleteUser(Integer userId);

    void changePassword(Integer userId, String oldPassword, String newPassword);

    void forgotPassword(String email);

    void resetPassword(Integer userId);
}
