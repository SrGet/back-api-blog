package com.api.blog.Mappers;

import com.api.blog.DTOs.CommentResponse;
import com.api.blog.Model.Comments;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    public CommentResponse toResponseDTO (Comments comment, Long postId, boolean owner, boolean alreadyLiked, Long likesAmount){
        return CommentResponse.builder()
                .id(comment.getId())
                .postId(postId)
                .ownerUsername(comment.getUser().getUsername())
                .createdAt(comment.getCreatedAt())
                .message(comment.getMessage())
                .owner(owner)
                .liked(alreadyLiked)
                .likesAmount(likesAmount)
                .imgCommentUrl(comment.getImgUrl())
                .imgOwnerUrl(comment.getUser().getProfileImgKey())
                .deletedAt(comment.getDeleted_at())
                .build();
    }
}
