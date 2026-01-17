package com.api.blog.DTOs;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class PostResponseDTO {

    private Long id;
    private String message;
    private String user;
    private String imgUrl;
    private boolean likedByCurrentUser;
    private Long likes;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
