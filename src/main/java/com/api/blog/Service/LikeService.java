package com.api.blog.Service;

import com.api.blog.ErrorHandling.customExceptions.ResourceNotFoundException;
import com.api.blog.DTOs.LikeResponseDTO;
import com.api.blog.Model.Post;
import com.api.blog.Model.PostLike;
import com.api.blog.Model.User;
import com.api.blog.Repositories.LikePostRepository;
import com.api.blog.Repositories.PostRepository;
import com.api.blog.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
@Slf4j
public class LikeService {

    private final LikePostRepository likePostRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public LikeResponseDTO toggleLike(Long idPost, String currentUser){

        Post post = postRepository.findById(idPost).orElseThrow(() -> new NoSuchElementException("Post no found for id: " + idPost));
        User user = userRepository.findByUsername(currentUser).orElseThrow(
                () -> new ResourceNotFoundException("Couldn't find user: " + currentUser));

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
        log.info("Counting likes for User: {} and Post: {}",user.getUsername(),post.getId());
        return likePostRepository.existsByUserAndPost(user,post);

    }

}
