package com.module5.team2.controllers;


import com.module5.team2.dto.request.*;
import com.module5.team2.dto.response.LoginResponse;
import com.module5.team2.dto.response.UserProfileResponse;
import com.module5.team2.entity.UserEntity;
import com.module5.team2.enums.Role;
import com.module5.team2.enums.Status;
import com.module5.team2.security.jwt.CustomUserDetails;
import com.module5.team2.security.jwt.JwtTokenProvider;
import com.module5.team2.service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * CUSTOMER & SUPPLIER đăng ký
     */
    @PostMapping("/public/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        UserEntity user = userService.register(request);

        return ResponseEntity.ok(
                UserProfileResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .phone(user.getPhone())
                        .name(user.getName())
                        .role(user.getRole().name())
                        .status(user.getStatus().name())
                        .build()
        );
    }

    /**
     * Login dùng chung cho TẤT CẢ role
     */
    @PostMapping("/public/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        String jwt = jwtTokenProvider.generateToken(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        LoginResponse response = new LoginResponse(
                jwt,
                userDetails.getUsername(),
                userDetails.getUserEntity().getRole().name()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * LOGIN DÀNH RIÊNG CHO ADMIN
     */
    @PostMapping("/admin/login")
    public ResponseEntity<?> adminLogin(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // Kiểm tra có là Admin không
        if (userDetails.getUserEntity().getRole() != Role.ADMIN) {
            return ResponseEntity.status(403).body("Truy cập bị từ chối: Chỉ dành cho Quản trị viên");
        }

        String jwt = jwtTokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new LoginResponse(
                jwt,
                userDetails.getUsername(),
                userDetails.getUserEntity().getRole().name()
        ));
    }

    /**
     * ADMIN tạo STAFF
     */

    @PostMapping("/admin/users/staff")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserProfileResponse> createStaff(
            @RequestBody CreateStaffRequest request) {
        UserEntity staff = userService.createStaff(request);

        return ResponseEntity.ok(
                UserProfileResponse.builder()
                        .id(staff.getId())
                        .username(staff.getUsername())
                        .email(staff.getEmail())
                        .phone(staff.getPhone())
                        .name(staff.getName())
                        .age(staff.getAge())
                        .role(staff.getRole().name())
                        .address(staff.getAddress())
                        .status(staff.getStatus().name())
                        .salary(staff.getSalary())
                        .build()
        );
    }

    @PutMapping("/admin/users/staff/{id}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> resetStaffPassword(@PathVariable Integer id) {
        userService.resetStaffPassword(id);

        return ResponseEntity.ok("Reset mật khẩu về mặc định thành công");
    }

//    @GetMapping("/admin/users")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<?> searchUsers (
//            @RequestParam(required = false) String keyword) {
//
//        return ResponseEntity.ok(
//                userService.searchUsers(keyword)
//                        .stream()
//                        .map(user -> UserProfileResponse.builder()
//                                .id(user.getId())
//                                .username(user.getUsername())
//                                .email(user.getEmail())
//                                .phone(user.getPhone())
//                                .name(user.getName())
//                                .role(user.getRole().name())
//                                .status(user.getStatus().name())
//                                .salary(user.getSalary())
//                                .build()
//                        )
//                        .toList()
//        );
//    }

    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> searchUsers(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) { // Mặc định 10 record/trang

        Page<UserEntity> userPage = userService.searchUsers(keyword, pageable);

        // Map Page<Entity> sang Page<DTO> để trả về đầy đủ thông tin phân trang cho Frontend
        Page<UserProfileResponse> responsePage = userPage.map(user ->
                UserProfileResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .phone(user.getPhone())
                        .name(user.getName())
                        .age(user.getAge())
                        .role(user.getRole().name())
                        .address(user.getAddress())
                        .status(user.getStatus().name())
                        .salary(user.getSalary())
                        .build()
        );

        return ResponseEntity.ok(responsePage);
    }

    /**
     * PROFILE – thông tin user đang đăng nhập
     */
    @GetMapping("/user/profile")
    public ResponseEntity<UserProfileResponse> profile(
            Authentication authentication) {
        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();
        UserEntity user = userDetails.getUserEntity();

        return ResponseEntity.ok(
                UserProfileResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .phone(user.getPhone())
                        .name(user.getName())
                        .age(user.getAge())
                        .address(user.getAddress())
                        .role(user.getRole().name())
                        .status(user.getStatus().name())
                        .salary(user.getSalary())
                        .build()
        );
    }

    @PutMapping("/user/profile")
    public ResponseEntity<UserProfileResponse> updateOwnProfile(
            Authentication authentication,
            @RequestBody UpdateUserRequest request) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer currentUserId = userDetails.getUserEntity().getId();

        UserEntity updatedUser = userService.updateUser(currentUserId, request);

        return ResponseEntity.ok(
                UserProfileResponse.builder()
                        .id(updatedUser.getId())
                        .username(updatedUser.getUsername())
                        .email(updatedUser.getEmail())
                        .phone(updatedUser.getPhone())
                        .name(updatedUser.getName())
                        .age(updatedUser.getAge())
                        .address(updatedUser.getAddress())
                        .role(updatedUser.getRole().name())
                        .status(updatedUser.getStatus().name())
                        .salary(updatedUser.getSalary())
                        .build()
        );
    }

    /**
     * UPDATE USER
     */
    @PutMapping("/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserProfileResponse> updateUser(
            @PathVariable Integer id,
            @RequestBody UpdateUserRequest request) {
        UserEntity updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(
                UserProfileResponse.builder()
                        .id(updatedUser.getId())
                        .username(updatedUser.getUsername())
                        .email(updatedUser.getEmail())
                        .phone(updatedUser.getPhone())
                        .name(updatedUser.getName())
                        .age(updatedUser.getAge())
                        .address(updatedUser.getAddress())
                        .role(updatedUser.getRole().name())
                        .status(updatedUser.getStatus().name())
                        .salary(updatedUser.getSalary())
                        .build()
        );
    }

    /**
     * CHANGE STATUS (Block/Active user)
     */
    @PatchMapping("/admin/users/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> changeStatus(
            @PathVariable Integer id,
            @RequestParam Status status) {
        userService.changeStatus(id, status);
        return ResponseEntity.ok("Cập nhật trạng thái thành công sang: " + status);
    }

    /**
     * DELETE USER
     */
    @DeleteMapping("/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("Xóa người dùng thành công");
    }

    /**
     * CHANGE PASSWORD
     */
    @PutMapping("/user/change-password")
    public ResponseEntity<?> changePassword(Authentication authentication, @RequestBody ChangePasswordRequest request) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer userId = userDetails.getUserEntity().getId();
        userService.changePassword(userId, request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok("Đổi mật khẩu thành công");
    }

    /**
     * FORGOT PASSWORD
     */
    @PostMapping("/public/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        userService.forgotPassword(request.getEmail());
        return ResponseEntity.ok("Mật khẩu mới đã được gửi về email");
    }





}
