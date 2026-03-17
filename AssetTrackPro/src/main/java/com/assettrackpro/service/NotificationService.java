package com.assettrackpro.service;

import com.assettrackpro.entity.Notification;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface NotificationService {

    List<Notification> getNotifications(Authentication authentication);

    long getUnreadCount(Authentication authentication);

    void markAsRead(Long id);

    void createNotification(String message, String email);
}