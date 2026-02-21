package com.api.blog.Service;

import com.api.blog.DTOs.PostResponseDTO;
import com.api.blog.Mappers.PostMapper;
import com.api.blog.Model.Post;
import com.api.blog.Model.User;
import com.api.blog.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedService {

    private final PostService postService;
    private final UserRepository userRepository;


    public Page<PostResponseDTO> getUserFeed(int pageNo, int pageSize, String currentUser){

        User authUser = userRepository.findByUsername(currentUser).orElseThrow(() -> new NoSuchElementException("User not found."));

        Page<Post> posts = postService.getLastsPosts(PageRequest.of(pageNo-1,pageSize));

        return posts.map(post -> postService.getPostDTO(post, authUser));

    }

    public Page<PostResponseDTO> getUserFeed(int pageNo, int pageSize, String currentUsername, String targetUsername){

        User authUser = userRepository.findByUsername(currentUsername).orElseThrow(() -> new NoSuchElementException("User not found."));

        Page<Post> posts = postService.getUserPosts(PageRequest.of(pageNo-1,pageSize), targetUsername);

        log.info("Getting user posts successful with pageNo: {}, pageSize: {}, Returning DTOs",pageNo-1,pageSize);

        return posts.map(post -> postService.getPostDTO(post, authUser));

    }



}
