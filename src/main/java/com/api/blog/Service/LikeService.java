package com.api.blog.Service;

import com.api.blog.Model.Post;
import com.api.blog.Model.PostLike;
import com.api.blog.Model.User;
import com.api.blog.Repositories.LikePostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikePostRepository likePostRepository;
    private final PostService postService;
    private final UserService userService;

    public String likePost(Long idPost){

        Post post = postService.getPostEntity(idPost);
        User user = userService.getUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

        if(likePostRepository.existsByUserAndPost(user,post)){
            return "Already liked.";
        }

        PostLike like = PostLike.builder()
                .user(user)
                .post(post)
                .likedAt(LocalDateTime.now())
                .build();

        likePostRepository.save(like);
        return "Liked.";

    }



}
