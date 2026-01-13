package com.api.blog.DTOs;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LikeResponseDTO {

    private boolean liked;
    private Long likeAmount;
}
