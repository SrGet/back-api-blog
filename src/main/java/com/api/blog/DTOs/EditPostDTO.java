package com.api.blog.DTOs;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EditPostDTO {

    private Long postId;
    private String newMessage;
}
