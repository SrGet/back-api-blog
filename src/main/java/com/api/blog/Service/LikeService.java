package com.api.blog.Service;

import com.api.blog.ErrorHandling.customExceptions.ResourceNotFoundException;
import com.api.blog.DTOs.LikeResponseDTO;
import com.api.blog.Model.*;
import com.api.blog.Repositories.*;
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
    private final CommentLikeRepository commentLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final StringRedisTemplate redisTemplate;




    @Transactional
    public LikeResponseDTO togglePostLike(Long idPost, String currentUser){

        Post post = postRepository.findById(idPost).orElseThrow(() -> new NoSuchElementException("Post no found for id: " + idPost));
        User user = userRepository.findByUsername(currentUser).orElseThrow(
                () -> new ResourceNotFoundException("Couldn't find user: " + currentUser));

        if(likePostRepository.existsByUserAndPost(user,post)){
            likePostRepository.deleteByUserAndPost(user,post);
            Long likesAmount = redisTemplate.opsForValue().decrement("post:likes:amount:" + idPost);
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
        Long likesAmount = redisTemplate.opsForValue().increment("post:likes:amount:" + idPost);
        return LikeResponseDTO.builder()
                .liked(true)
                .likeAmount(likesAmount)
                .build();

    }

    @Transactional
    public LikeResponseDTO toggleCommentLike(Long commentId, String currentUser){

        User authUser = userRepository.findByUsername(currentUser).orElseThrow(
                () -> new ResourceNotFoundException("Couldn't find user: " + currentUser));

        Comments comment = commentRepository.findById(commentId).orElseThrow(
                () -> new ResourceNotFoundException("Comment not found."));

        // ** Delete like if already liked**
        if(commentLikeRepository.existsByUserAndComments(authUser,comment)){

            commentLikeRepository.deleteByUserAndComments(authUser,comment);
            Long likesCount = redisTemplate.opsForValue().decrement("comment:likes:amount:"+ commentId);

            return LikeResponseDTO.builder()
                    .liked(false)
                    .likeAmount(likesCount)
                    .build();
        }

        // ** Otherwise add like **
        CommentLike commentLike = CommentLike.builder()
                .user(authUser)
                .comments(comment)
                .build();
        commentLikeRepository.save(commentLike);
        Long likesCount = redisTemplate.opsForValue().increment("comment:likes:amount:" + commentId);
        return LikeResponseDTO.builder()
                .liked(true)
                .likeAmount(likesCount)
                .build();

    }

    public boolean isPostLiked(User user, Post post){
        return likePostRepository.existsByUserAndPost(user,post);

    }

    public boolean isCommentLiked(User user, Comments comment){
        return commentLikeRepository.existsByUserAndComments(user,comment);

    }

    public Long getPostLikesCount(Post post){
        String count = redisTemplate.opsForValue().get("post:likes:amount:" + post.getId());
        if (count != null){
            return Long.parseLong(count);
        }

        Long dbCount = likePostRepository.countByPost(post);
        redisTemplate.opsForValue().set("post:likes:amount:"+post.getId(), dbCount.toString());
        return dbCount;
    }

    public Long getCommentLikesCount(Comments comment){
        String count = redisTemplate.opsForValue().get("comment:likes:amount:" + comment.getId());
        if (count != null){
            return Long.parseLong(count);
        }

        Long dbCount = commentLikeRepository.countByComments(comment);
        redisTemplate.opsForValue().set("comment:likes:amount:"+comment.getId(), dbCount.toString());
        return dbCount;
    }

}
