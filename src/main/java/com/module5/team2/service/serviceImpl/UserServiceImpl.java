package com.module5.team2.service.serviceImpl;

import com.module5.team2.dto.request.UpdateUserRequest;
import com.module5.team2.entity.UserEntity;
import com.module5.team2.repository.UserRepository;
import com.module5.team2.service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.module5.team2.dto.request.CreateStaffRequest;
import com.module5.team2.dto.request.RegisterRequest;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

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
     * CUSTOMER & SUPPLIER đăng ký
     */
    @Override
    public UserEntity register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username đã tồn tại");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        if (request.getRole() == null ||
                (request.getRole() != UserEntity.Role.CUSTOMER &&
                        request.getRole() != UserEntity.Role.SUPPLIER)) {
            throw new RuntimeException("Role không hợp lệ");
        }

        UserEntity user = UserEntity.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .phone(request.getPhone())
                .name(request.getName())
                .role(request.getRole())
                .status(UserEntity.Status.active)
                .build();

        return userRepository.save(user);
    }

    /**
     * ADMIN tạo STAFF
     */
    @Override
    public UserEntity createStaff(CreateStaffRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username đã tồn tại");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        if (request.getAge() == null ||
                request.getAge() <= 18 || request.getAge() >= 60) {
            throw new RuntimeException("Tuổi phải > 18 và < 60");
        }

        if (request.getSalary() == null ||
                request.getSalary() <= 0 ||
                request.getSalary() >= 100000000) {
            throw new RuntimeException("Lương không hợp lệ");
        }
        UserEntity staff = UserEntity.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(DEFAULT_STAFF_PASSWORD))
                .email(request.getEmail())
                .name(request.getName())
                .age(request.getAge())
                .phone(request.getPhone())
                .address(request.getAddress())
                .salary(request.getSalary())
                .role(UserEntity.Role.STAFF)
                .status(UserEntity.Status.active)
                .build();

        return userRepository.save(staff);
    }

    @Override
    public void resetStaffPassword(Integer staffId) {
        UserEntity staff = userRepository.findById(staffId)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy nhân viên"));

        if (staff.getRole() != UserEntity.Role.STAFF) {
            throw new RuntimeException("Chỉ reset mật khẩu cho STAFF");
        }

        staff.setPassword(
                passwordEncoder.encode(DEFAULT_STAFF_PASSWORD)
        );

        userRepository.save(staff);
    }

    @Override
    public UserEntity findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy user"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserEntity> searchUsers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            // nếu không nhập gì => trả về tất cả
            return userRepository.findAll();
        }

        return userRepository
                .findByUsernameContainingIgnoreCaseOrNameContainingIgnoreCase(
                        keyword,
                        keyword
                );
    }
}
