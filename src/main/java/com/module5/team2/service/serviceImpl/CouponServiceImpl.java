package com.module5.team2.service.serviceImpl;

import com.module5.team2.dto.request.CouponRequest;
import com.module5.team2.dto.response.CouponResponse;
import com.module5.team2.entity.Coupon;
import com.module5.team2.entity.UserEntity;
import com.module5.team2.exception.BusinessException;
import com.module5.team2.repository.CouponRepository;
import com.module5.team2.repository.OrderItemRepository;
import com.module5.team2.repository.UserRepository;
import com.module5.team2.service.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CouponServiceImpl implements CouponService {
    private final CouponRepository couponRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;

    // ================= CREATE =================
    @Override
    public CouponResponse create(String username, CouponRequest request) {

        if (couponRepository.existsByCode(request.getCode())) {
            throw new BusinessException("Mã đã tồn tại");
        }

        UserEntity supplier = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("Không tìm thấy supplier"));

        Coupon coupon = Coupon.builder()
                .code(request.getCode())
                .value(request.getValue())
                .minOrderValue(request.getMinOrderValue())
                .usageLimit(request.getUsageLimit())
                .expiresAt(request.getExpiresAt())
                .supplier(supplier)
                .build();

        couponRepository.save(coupon);

        return mapToDTO(coupon);
    }

    // ================= GET LIST =================
    @Override
    public Page<CouponResponse> getCoupons(
            String username,
            int page,
            int size,
            String keyword,
            Boolean status
    ) {

        UserEntity supplier = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("Không tìm thấy supplier"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Coupon> coupons;

        if (status != null) {
            coupons = couponRepository
                    .findBySupplierAndCodeContainingIgnoreCaseAndIsActive(
                            supplier, keyword, status, pageable
                    );
        } else {
            coupons = couponRepository
                    .findBySupplierAndCodeContainingIgnoreCase(
                            supplier, keyword, pageable
                    );
        }

        return coupons.map(this::mapToDTO);
    }

    // ================= TOGGLE =================
    @Override
    public void toggle(Long id) {
        Coupon coupon = getCoupon(id);

        // nếu hết hạn thì không cho bật lại
        if (coupon.getExpiresAt() != null &&
                coupon.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Mã đã hết hạn, không thể kích hoạt");
        }

        coupon.setIsActive(!coupon.getIsActive());
    }

    // ================= DELETE =================
    @Override
    public void delete(Long id) {

        Coupon coupon = getCoupon(id);

//        boolean isUsed = orderItemRepository.existsByCouponCode(coupon.getCode());
//
//        if (isUsed) {
//            throw new BusinessException("Mã đã được sử dụng, không thể xóa");
//        }

        couponRepository.delete(coupon);
    }

    @Override
    public List<CouponResponse> getCouponsBySupplierId(Long supplierId) {

        UserEntity supplier = userRepository.findById(supplierId.intValue())
                .orElseThrow(() -> new BusinessException("Không tìm thấy supplier"));

        List<Coupon> coupons = couponRepository
                .findBySupplierAndIsActiveTrue(supplier);

        // lọc thêm: chưa hết hạn
        return coupons.stream()
                .filter(c -> c.getExpiresAt() == null ||
                        c.getExpiresAt().isAfter(LocalDateTime.now()))
                .filter(c -> c.getUsageLimit() == null ||
                        c.getUsedCount() < c.getUsageLimit())
                .map(this::mapToDTO)
                .toList();
    }

    // ================= PRIVATE =================
    private Coupon getCoupon(Long id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy mã"));
    }

    private CouponResponse mapToDTO(Coupon c) {
        return CouponResponse.builder()
                .id(c.getId())
                .code(c.getCode())
                .value(c.getValue())
                .minOrderValue(c.getMinOrderValue())
                .usageLimit(c.getUsageLimit())
                .usedCount(c.getUsedCount())
                .isActive(c.getIsActive())
                .expiresAt(c.getExpiresAt())
                .createdAt(c.getCreatedAt())
                .build();
    }
}
