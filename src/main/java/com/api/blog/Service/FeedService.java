package com.api.blog.Service;

import com.api.blog.DTOs.PostResponseDTO;
import com.api.blog.Mappers.PostMapper;
import com.api.blog.Model.Post;
import com.api.blog.Model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final PostService postService;
    private final UserService userService;

    public List<PostResponseDTO> getUserFeed(int pageNo, int pageSize){

        // Here goes all the logic to create personalized user feed

        User authUser = userService.getUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        List<Post> posts = postService.getLastsPosts(PageRequest.of(pageNo-1,pageSize));


        return posts.stream().map(post -> postService.getPostDTO(post.getId())).toList();







    }


}
