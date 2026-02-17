package com.api.blog.Mappers;

import com.api.blog.DTOs.NewPostDto;
import com.api.blog.DTOs.PostResponseDTO;
import com.api.blog.Model.Post;
import com.api.blog.Model.User;
import com.api.blog.Service.LikeService;
import com.api.blog.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostMapper {

    public PostResponseDTO toResponseDto(Post post,boolean isLikedByCurrentUser,
                                         boolean owner, String imgUserUrl, Long likesAmount, Long commentsAmount ){

        if (post == null){
            return null;
        }

        return PostResponseDTO.builder()
                .id(post.getId())
                .message(post.getMessage())
                .imgPostUrl(post.getImageUrl() != null ? "/file/"+post.getImageUrl() : null)
                .imgUserUrl(imgUserUrl != null ? "/file/"+post.getImageUrl() : null)
                .user(post.getUser().getUsername())
                .likes(likesAmount)
                .commentsAmount(commentsAmount)
                .likedByCurrentUser(isLikedByCurrentUser)
                .owner(owner)
                .deleted_at(post.getDeleted_at())
                .createdAt(post.getCreatedAt())
                .modifiedAt(post.getModifiedAt())
                .build();
    }





}
