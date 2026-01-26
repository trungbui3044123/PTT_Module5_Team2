package com.module5.team2.service.serviceImpl;

import com.module5.team2.dto.request.UpdateUserRequest;
import com.module5.team2.entity.UserEntity;
import com.module5.team2.repository.UserRepository;
import com.module5.team2.service.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    /**
     * UPDATE USER
     */
    @Override
    public UserEntity updateUser(Integer userId, UpdateUserRequest request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
        user.setName(request.getName());
        user.setAge(request.getAge());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        return userRepository.save(user);
    }


    /**
     * BLOCK / ACTIVE / BANNED
     */
    @Override
    public void changeStatus(Integer userId, UserEntity.Status status) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
        user.setStatus(status);
        userRepository.save(user);
    }

    /**
     * DELETE USER
     */
    @Override
    public void deleteUser(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User không tồn tại");
        }
        userRepository.deleteById(userId);
    }

}
