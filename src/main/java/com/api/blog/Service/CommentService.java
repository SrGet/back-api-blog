package com.api.blog.Service;

import com.api.blog.ErrorHandling.customExceptions.ResourceNotFoundException;
import com.api.blog.DTOs.NewCommentRequest;
import com.api.blog.DTOs.CommentResponse;
import com.api.blog.Mappers.CommentMapper;
import com.api.blog.Model.Comments;
import com.api.blog.Model.Post;
import com.api.blog.Model.User;
import com.api.blog.Repositories.CommentRepository;
import com.api.blog.Repositories.PostRepository;
import com.api.blog.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final MinIOService minIOService;
    private final CommentMapper commentMapper;
    private final StringRedisTemplate redisTemplate;


    public CommentResponse create(String currentUsername, NewCommentRequest newComment){

        Post post = postRepository.findById(newComment.getPostId()).orElseThrow(() -> new NoSuchElementException("Post not found."));

        User currentUser = userRepository.findByUsername(currentUsername).orElseThrow(
                () -> new ResourceNotFoundException("Couldn't find user: " + currentUsername));

        String imgKey = minIOService.uploadFile(newComment.getFile());

        Comments comment = Comments.builder()
                .user(currentUser)
                .post(post)
                .message(newComment.getMessage())
                .imgUrl(imgKey)
                .build();

        try{
            Comments commentCreated = commentRepository.save(comment);
            redisTemplate.opsForValue().increment("comments:amount:" + post.getId());
            return getCommentDTO(commentCreated, post.getId(),currentUsername);
        } catch (Exception e) {
            log.error("Couldn't save post. Deleting file. Reason: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public CommentResponse getCommentDTO(Comments comment, Long postId, String currentUser){

        String username = comment.getUser().getUsername();
        boolean isOwner = username.equals(currentUser);
        return commentMapper.toResponseDTO(comment, postId, username,isOwner);
    }

    public Page<CommentResponse> getPostComments(int pageNo, int pageSize, Long postId, String currentUser){

        Page<Comments> commentsList = commentRepository.findAllByPostId(PageRequest.of(pageNo,pageSize), postId);
        if(commentsList != null){
            return commentsList.map(comment -> getCommentDTO(comment, postId, currentUser));
        }else {
            return null;
        }

    }

    public void delete(Long commentId){
        Comments comment = commentRepository.findById(commentId).orElseThrow(
                () -> new ResourceNotFoundException("Comment not found"));

        comment.setDeleted_at(LocalDateTime.now());
        commentRepository.save(comment);
        redisTemplate.opsForValue().decrement("comments:amount:"+ comment.getPost().getId());


    }

    public Long getCommentsAmount(Long idPost){
        String commentsAmount = redisTemplate.opsForValue().get("comments:amount:"+idPost);
        if (commentsAmount != null){
            return Long.parseLong(commentsAmount);
        }

        Long dbCount = commentRepository.countByPostId(idPost);
        redisTemplate.opsForValue().set("comments:amount:"+idPost, dbCount.toString());
        return dbCount;
    }

}
