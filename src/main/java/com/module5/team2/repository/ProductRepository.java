package com.module5.team2.repository;

import com.module5.team2.entity.ProductEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.module5.team2.enums.ProductStatus;
import com.module5.team2.enums.Status;
import java.util.List;





public interface ProductRepository  extends JpaRepository<ProductEntity, Integer> {

    Page<ProductEntity> findByNameContaining(String name, Pageable pageable);
    Page<ProductEntity> findBySupplierId(Integer supplierId, Pageable pageable);
    Page<ProductEntity> findByNameContainingAndStatus(String name,ProductStatus status, Pageable pageable);
    Page<ProductEntity> findByStatus(ProductStatus status, Pageable pageable);
    Page<ProductEntity> findByStatusAndSupplier_Status(ProductStatus status, Status userstatus,Pageable pageable);
    Page<ProductEntity> findBySupplierIdAndNameContainingIgnoreCase(Integer id, String keyword, Pageable pageable);
    Page<ProductEntity> findByCategoryAndStatus(String category, ProductStatus status, Pageable pageable);
    Page<ProductEntity> findByPriceLessThanAndStatusAndCategory(Double price, ProductStatus status, String category, Pageable pageable);
    Page<ProductEntity>  findBySupplier_IdAndStatus(Integer supplierId,ProductStatus status, Pageable pageable);
    @Query("SELECT p.supplier.id FROM ProductEntity p WHERE p.id = :productId")
    Integer findSupplierIdByProductId(@Param("productId") Integer productId);


}
