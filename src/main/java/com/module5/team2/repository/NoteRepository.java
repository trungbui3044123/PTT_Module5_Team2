package com.module5.team2.repository;

import com.module5.team2.entity.Note;
import com.module5.team2.enums.ViolationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long> {

    long countBySupplierIdAndStatus(Integer supplierId, ViolationStatus status);

}
