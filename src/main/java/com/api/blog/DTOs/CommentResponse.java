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
    private String ownerUsername;
    private Long postId;
    private String message;
    private String imgCommentUrl;
    private String imgOwnerUrl;
    private boolean owner;
    private Long likesAmount;
    private boolean liked;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

}
