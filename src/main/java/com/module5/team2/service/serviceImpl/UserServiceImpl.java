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
    private static final String DEFAULT_STAFF_PASSWORD = "123456@Abc";
    private final PasswordEncoder passwordEncoder;

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
    /**
     * CHANGE PASSWORD
     */
    @Override
    public void changePassword(Integer userId, String oldPassword, String newPassword) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Mật khẩu cũ không đúng");
        }
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new RuntimeException("Mật khẩu mới không được trùng mật khẩu cũ");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * FORGOT PASSWORD
     */
    @Override
    public void forgotPassword(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));
        String defaultPassword = "123456@Abc";
        user.setPassword(passwordEncoder.encode(defaultPassword));
        userRepository.save(user);
        // giả lập gửi mail
        System.out.println(">>> Gửi mail tới: " + email);
        System.out.println(">>> Mật khẩu mới: " + defaultPassword);
    }
    /**
     * RESET PASSWORD
     */
    @Override
    public void resetPassword(Integer userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
        user.setPassword(passwordEncoder.encode(DEFAULT_STAFF_PASSWORD));
        userRepository.save(user);
    }


}
