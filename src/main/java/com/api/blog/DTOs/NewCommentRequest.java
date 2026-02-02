package com.api.blog.DTOs;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
public class NewCommentRequest {

    private Long postId;
    private String message;
    private MultipartFile file;
}
