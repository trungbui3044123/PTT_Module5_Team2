package com.module5.team2.service.serviceImpl;

import com.module5.team2.dto.response.NotificationResponse;
import com.module5.team2.entity.Notification;
import com.module5.team2.entity.UserEntity;
import com.module5.team2.exception.BusinessException;
import com.module5.team2.exception.ResourceNotFoundException;
import com.module5.team2.repository.NotificationRepository;
import com.module5.team2.service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public void createNotification(
            UserEntity user,
            String title,
            String content,
            String type
    ) {

        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .content(content)
                .type(type)
                .build();

        notificationRepository.save(notification);

        // Future: WebSocket
    }

    @Override
    public Page<NotificationResponse> getUserNotifications(
            Integer userId,
            Pageable pageable
    ) {

        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::toResponse);
    }

    @Override
    public void markAsRead(Long notificationId, Integer userId) {

        Notification notification = notificationRepository
                .findById(notificationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Không tìm thấy thông báo"));

        if (!notification.getUser().getId().equals(userId)) {
            throw new BusinessException("Bạn không có quyền cập nhật thông báo này");
        }

        notification.setIsRead(true);
    }

    @Override
    public void markAllAsRead(Integer userId) {

        notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId, Pageable.unpaged())
                .forEach(notification -> notification.setIsRead(true));
    }

    private NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .type(notification.getType())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
