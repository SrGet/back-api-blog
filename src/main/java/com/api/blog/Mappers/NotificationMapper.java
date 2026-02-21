package com.api.blog.Mappers;

import com.api.blog.DTOs.NotificationResponse;
import com.api.blog.Model.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponse toDto(Notification notification){

        return NotificationResponse.builder()
                .id(notification.getId())
                .senderUsername(notification.getSender().getUsername())
                .senderImgUrl(notification.getSender().getProfileImgKey())
                .message(notification.getMessage())
                .alreadyRead(notification.isAlreadyRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
