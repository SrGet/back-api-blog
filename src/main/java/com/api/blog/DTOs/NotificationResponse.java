package com.api.blog.DTOs;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Setter
@Getter
public class NotificationResponse {

    private Long id;
    private String senderUsername;
    private String senderImgUrl;
    private String message;
    private boolean alreadyRead;
    private LocalDateTime createdAt;
}
