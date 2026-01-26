package com.api.blog.DTOs;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class UserProfileDTO {

    private String profileImgUrl;
    private String username;
    private Long following;
    private Long followers;
    private Long postsCount;
    private boolean followed;

}
