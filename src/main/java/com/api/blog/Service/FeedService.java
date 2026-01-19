package com.api.blog.Service;

import com.api.blog.DTOs.PostResponseDTO;
import com.api.blog.Mappers.PostMapper;
import com.api.blog.Model.Post;
import com.api.blog.Model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedService {

    private final PostService postService;


    public List<PostResponseDTO> getUserFeed(int pageNo, int pageSize){

        List<Post> posts = postService.getLastsPosts(PageRequest.of(pageNo-1,pageSize));
        log.info("Getting lastPosts successful with pageNo: {}, pageSize: {}, Returning DTOs",pageNo-1,pageSize);

        return posts.stream().map(post -> postService.getPostDTO(post.getId())).toList();







    }


}
