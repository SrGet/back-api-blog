package com.api.blog.DTOs;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDTO {

    private Long id;
    private String message;
    private String user;
    private String imgPostUrl;
    private String imgUserUrl;
    private boolean likedByCurrentUser;
    private Long commentsAmount;
    private boolean owner;
    private LocalDateTime deleted_at;
    private Long likes;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
