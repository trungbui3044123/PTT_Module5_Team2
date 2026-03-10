package com.module5.team2.service.service;

import com.module5.team2.dto.response.NotificationResponse;
import com.module5.team2.entity.Order;
import com.module5.team2.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    void createNotification(
            UserEntity user,
            String title,
            String content,
            String type,
            Order order
    );

    Page<NotificationResponse> getUserNotifications(
            Integer userId,
            Pageable pageable
    );

    void markAsRead(Long notificationId, Integer userId);

    void markAllAsRead(Integer userId);
}
