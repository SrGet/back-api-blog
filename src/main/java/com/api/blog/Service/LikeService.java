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
import org.springframework.data.redis.core.StringRedisTemplate;
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
    private final StringRedisTemplate redisTemplate;


    @Transactional
    public LikeResponseDTO toggleLike(Long idPost, String currentUser){

        Post post = postRepository.findById(idPost).orElseThrow(() -> new NoSuchElementException("Post no found for id: " + idPost));
        User user = userRepository.findByUsername(currentUser).orElseThrow(
                () -> new ResourceNotFoundException("Couldn't find user: " + currentUser));

        if(likePostRepository.existsByUserAndPost(user,post)){
            likePostRepository.deleteByUserAndPost(user,post);
            Long likesAmount = redisTemplate.opsForValue().decrement("likes:amount:" + idPost);
            return LikeResponseDTO.builder()
                    .liked(false)
                    .likeAmount(likesAmount)
                    .build();
        }

        PostLike like = PostLike.builder()
                .user(user)
                .post(post)
                .likedAt(LocalDateTime.now())
                .build();

        likePostRepository.save(like);
        Long likesAmount = redisTemplate.opsForValue().increment("likes:amount:" + idPost);
        return LikeResponseDTO.builder()
                .liked(true)
                .likeAmount(likesAmount)
                .build();

    }

    public boolean isLiked(User user, Post post){
        return likePostRepository.existsByUserAndPost(user,post);

    }

    public Long getLikesCount(Post post){
        String count = redisTemplate.opsForValue().get("likes:amount:" + post.getId());
        if (count != null){
            return Long.parseLong(count);
        }

        Long dbCount = likePostRepository.countByPost(post);
        redisTemplate.opsForValue().set("likes:amount:"+post.getId(), dbCount.toString());
        return dbCount;
    }

}
