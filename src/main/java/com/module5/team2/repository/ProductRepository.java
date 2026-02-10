package com.module5.team2.repository;

import com.module5.team2.entity.ProductEntity;
import com.module5.team2.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository  extends JpaRepository<ProductEntity, Integer> {
    Page<ProductEntity> findByNameContaining(String name, Pageable pageable);
    Page<ProductEntity> findBySupplierId(Integer supplierId, Pageable pageable);

    Page<ProductEntity> findBySupplierIdAndNameContainingIgnoreCase(Integer id, String keyword, Pageable pageable);
}
