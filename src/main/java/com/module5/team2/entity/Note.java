package com.module5.team2.entity;

import com.module5.team2.enums.ViolationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // có thể null
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private UserEntity customer;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private UserEntity supplier;

    @ManyToOne
    @JoinColumn(name = "staff_id", nullable = false)
    private UserEntity staff;

    @Column(columnDefinition = "TEXT")
    private String violation;

    @Enumerated(EnumType.STRING)
    private ViolationStatus status;

    private LocalDateTime createdAt;
    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
