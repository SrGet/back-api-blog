package com.api.blog.Mappers;

import com.api.blog.DTOs.CommentResponse;
import com.api.blog.Model.Comments;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    public CommentResponse toResponseDTO (Comments comment, Long postId, String username, boolean owner){
        return CommentResponse.builder()
                .id(comment.getId())
                .postId(postId)
                .username(username)
                .createdAt(comment.getCreatedAt())
                .message(comment.getMessage())
                .owner(owner)
                .imgUrl(comment.getImgUrl() != null ? "/file/" + comment.getImgUrl() : null )
                .deletedAt(comment.getDeleted_at())
                .build();
    }
}
