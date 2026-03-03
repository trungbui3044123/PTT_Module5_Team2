package com.module5.team2.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.module5.team2.enums.OrderStatus;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private UserEntity customer;
    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private UserEntity supplier;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private BigDecimal totalAmount;
    @Column(columnDefinition = "TEXT")
    private String rejectReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = OrderStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
