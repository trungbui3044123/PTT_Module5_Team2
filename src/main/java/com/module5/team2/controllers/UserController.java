package com.module5.team2.controllers;


import com.module5.team2.dto.request.CreateStaffRequest;
import com.module5.team2.dto.request.LoginRequest;
import com.module5.team2.dto.request.RegisterRequest;
import com.module5.team2.dto.response.LoginResponse;
import com.module5.team2.dto.response.UserProfileResponse;
import com.module5.team2.entity.UserEntity;
import com.module5.team2.security.jwt.CustomUserDetails;
import com.module5.team2.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.module5.team2.service.UserService;

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
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
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
                        .role(staff.getRole().name())
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
}
