package com.api.blog.DTOs;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Setter
@Getter
@Builder
public class CommentResponse {

    private Long id;
    private String username;
    private Long postId;
    private String message;
    private String imgUrl;
    private boolean owner;
    private LocalDateTime createdAt;

}
