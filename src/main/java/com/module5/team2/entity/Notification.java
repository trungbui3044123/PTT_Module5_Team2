package com.module5.team2.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.*;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String type; // hoặc Enum nếu muốn chặt chẽ hơn private Boolean isRead = false;

    private Boolean isRead;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        isRead = false;
        createdAt = LocalDateTime.now();
    }
}
