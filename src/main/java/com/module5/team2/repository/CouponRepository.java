package com.module5.team2.repository;

import com.module5.team2.entity.Coupon;
import com.module5.team2.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon,Long> {
    boolean existsByCode(String code);

    List<Coupon> findBySupplierId(Long supplierId);

    Optional<Coupon> findByCodeAndIsActiveTrue(String code);

    Page<Coupon> findBySupplierAndCodeContainingIgnoreCase(
            UserEntity supplier, String code, Pageable pageable);

    Page<Coupon> findBySupplierAndCodeContainingIgnoreCaseAndIsActive(
            UserEntity supplier, String code, Boolean isActive, Pageable pageable);
}
