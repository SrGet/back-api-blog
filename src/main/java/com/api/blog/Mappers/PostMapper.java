package com.api.blog.Mappers;

import com.api.blog.DTOs.NewPostDto;
import com.api.blog.DTOs.PostResponseDTO;
import com.api.blog.Model.Post;
import com.api.blog.Model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostMapper {

    public PostResponseDTO toResponseDto(Post post){
        if (post == null){
            return null;
        }

        return PostResponseDTO.builder()
                .id(post.getId())
                .message(post.getMessage())
                .imgUrl(post.getImageUrl())
                .user("@"+post.getUser().getUsername())
                .active(post.isActive())
                .createdAt(post.getCreatedAt())
                .modifiedAt(post.getModifiedAt())
                .build();
    }


}
