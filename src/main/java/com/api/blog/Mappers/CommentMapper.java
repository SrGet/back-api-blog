package com.api.blog.Mappers;

import com.api.blog.DTOs.CommentResponse;
import com.api.blog.Model.Comments;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    public CommentResponse toResponseDTO (Comments comment, Long postId, String currentUser, boolean owner){
        return CommentResponse.builder()
                .id(comment.getId())
                .postId(postId)
                .username(currentUser)
                .createdAt(comment.getCreatedAt())
                .message(comment.getMessage())
                .imgUrl(comment.getImgUrl() != null ? "/file/" + comment.getImgUrl() : null )
                .build();
    }
}
