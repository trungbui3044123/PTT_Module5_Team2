package com.module5.team2.service.serviceImpl;

import com.module5.team2.dto.request.ChangePasswordRequest;
import com.module5.team2.dto.request.UpdateUserRequest;
import com.module5.team2.entity.UserEntity;
import com.module5.team2.enums.Role;
import com.module5.team2.enums.Status;
import com.module5.team2.exception.BusinessException;
import com.module5.team2.exception.ResourceNotFoundException;
import com.module5.team2.repository.UserRepository;
import com.module5.team2.service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.module5.team2.dto.request.CreateStaffRequest;
import com.module5.team2.dto.request.RegisterRequest;
import org.springframework.transaction.annotation.Transactional;


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
                .orElseThrow(() -> new ResourceNotFoundException("User không tồn tại"));
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
    public void changeStatus(Integer userId, Status status) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User không tồn tại"));
        user.setStatus(status);
        userRepository.save(user);
    }

    /**
     * DELETE USER
     */
    @Override
    public void deleteUser(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User không tồn tại");
        }
        userRepository.deleteById(userId);
    }

    /**
     * CHANGE PASSWORD
     */
    @Override
    public void changePassword(Integer userId, ChangePasswordRequest request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User không tồn tại"));
//        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
//            throw new BusinessException("Mật khẩu cũ không đúng");
//        }
//        if (passwordEncoder.matches(newPassword, user.getPassword())) {
//            throw new BusinessException("Mật khẩu mới không được trùng mật khẩu cũ");
//        }
//        user.setPassword(passwordEncoder.encode(newPassword));
//        userRepository.save(user);
        // Check mật khẩu cũ
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException("Mật khẩu cũ không đúng");
        }

        // Check confirm password
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("Mật khẩu xác nhận không khớp");
        }

        // Check trùng mật khẩu cũ
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BusinessException("Mật khẩu mới không được trùng mật khẩu cũ");
        }

        // 4. Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    /**
     * FORGOT PASSWORD
     */
    @Override
    public void forgotPassword(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Email không tồn tại"));
        String defaultPassword = "123456@Abc";
        user.setPassword(passwordEncoder.encode(defaultPassword));
        userRepository.save(user);
        // giả lập gửi mail
        System.out.println(">>> Gửi mail tới: " + email);
        System.out.println(">>> Mật khẩu mới: " + defaultPassword);
    }


    /**
     * CUSTOMER & SUPPLIER đăng ký
     */
    @Override
    public UserEntity register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username đã tồn tại");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email đã tồn tại");
        }

        if (request.getRole() == null ||
                (request.getRole() != Role.CUSTOMER &&
                        request.getRole() != Role.SUPPLIER)) {
            throw new BusinessException("Role không hợp lệ");
        }

        UserEntity user = UserEntity.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .phone(request.getPhone())
                .name(request.getName())
                .role(request.getRole())
                .status(Status.ACTIVE)
                .build();

        return userRepository.save(user);
    }

    /**
     * ADMIN tạo STAFF
     */
    @Override
    public UserEntity createStaff(CreateStaffRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username đã tồn tại");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email đã tồn tại");
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
                .role(Role.STAFF)
                .status(Status.ACTIVE)
                .build();

        return userRepository.save(staff);
    }

    @Override
    public void resetStaffPassword(Integer staffId) {
        UserEntity staff = userRepository.findById(staffId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Không tìm thấy nhân viên"));

        if (staff.getRole() != Role.STAFF) {
            throw new BusinessException("Chỉ reset mật khẩu cho STAFF");
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

//    @Override
//    @Transactional(readOnly = true)
//    public List<UserEntity> searchUsers(String keyword) {
//        if (keyword == null || keyword.trim().isEmpty()) {
//            // nếu không nhập gì => trả về tất cả
//            return userRepository.findAll();
//        }
//
//        return userRepository
//                .findByUsernameContainingIgnoreCaseOrNameContainingIgnoreCase(
//                        keyword,
//                        keyword
//                );
//    }


    @Override
    @Transactional(readOnly = true)
    public Page<UserEntity> searchUsers(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return userRepository.findAll(pageable);
        }

        return userRepository.findByUsernameContainingIgnoreCaseOrNameContainingIgnoreCase(
                keyword, keyword, pageable
        );
    }

    //Seed Post tạo dữ liệu admin mẫu
    @jakarta.annotation.PostConstruct
    public void initAdmin() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            UserEntity admin = UserEntity.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123")) // Mật khẩu để login Postman
                    .email("admin@gmail.com")
                    .phone("0999999999")
                    .name("System Admin")
                    .role(Role.ADMIN)
                    .status(Status.ACTIVE)
                    .build();
            userRepository.save(admin);
            System.out.println(">>> Đã khởi tạo tài khoản Admin mặc định: admin/admin123");
        }
    }
}
