package com.api.blog.Service;

import com.api.blog.DTOs.LikeResponseDTO;
import com.api.blog.Model.Post;
import com.api.blog.Model.PostLike;
import com.api.blog.Model.User;
import com.api.blog.Repositories.LikePostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikePostRepository likePostRepository;
    private final PostService postService;
    private final UserService userService;

    @Transactional
    public LikeResponseDTO toggleLike(Long idPost){

        Post post = postService.getPostEntity(idPost);
        User user = userService.getUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

        if(likePostRepository.existsByUserAndPost(user,post)){
            likePostRepository.deleteByUserAndPost(user,post);
            return LikeResponseDTO.builder()
                    .liked(false)
                    .likeAmount(likePostRepository.countByPost(post))
                    .build();
        }

        PostLike like = PostLike.builder()
                .user(user)
                .post(post)
                .likedAt(LocalDateTime.now())
                .build();

        likePostRepository.save(like);
        return LikeResponseDTO.builder()
                .liked(true)
                .likeAmount(likePostRepository.countByPost(post))
                .build();

    }

    public boolean isLiked(User user, Post post){
        return likePostRepository.existsByUserAndPost(user,post);

    }





}
