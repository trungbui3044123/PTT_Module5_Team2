package com.module5.team2.repository;

import com.module5.team2.entity.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ViolationRepository extends JpaRepository<Note, Long> {
    @Query("""
    SELECT n FROM Note n
    WHERE n.staff.id = :staffId
    AND (:customerId IS NULL OR n.customer.id = :customerId)
    AND (:supplierId IS NULL OR n.supplier.id = :supplierId)
    AND (:keyword IS NULL OR LOWER(n.violation) LIKE LOWER(CONCAT('%', :keyword, '%')))
""")
    Page<Note> searchViolationsByStaff(
            @Param("staffId") Long staffId,
            @Param("customerId") Integer customerId,
            @Param("supplierId") Integer supplierId,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("""
        SELECT n FROM Note n
        WHERE n.staff.id = :staffId
        ORDER BY n.createdAt DESC
    """)
    List<Note> findByStaffId(@Param("staffId") Long staffId);
}