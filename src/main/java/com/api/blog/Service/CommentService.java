package com.api.blog.Service;

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
import org.springframework.stereotype.Service;

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


    public CommentResponse create(String currentUsername, NewCommentRequest newComment){

        Post post = postRepository.findById(newComment.getPostId()).orElseThrow(() -> new NoSuchElementException("Post not found."));

        User currentUser = userRepository.findByUsername(currentUsername);
        String imgKey = minIOService.uploadFile(newComment.getFile());

        Comments comment = Comments.builder()
                .user(currentUser)
                .post(post)
                .message(newComment.getMessage())
                .imgUrl(imgKey)
                .build();

        try{
            Comments commentCreated = commentRepository.save(comment);
            log.info("Comment creating successful. Returning DTO");
            return getCommentDTO(commentCreated, post.getId(),currentUsername);
        } catch (Exception e) {
            log.error("Couldn't save post. Deleting file. Reason: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public CommentResponse getCommentDTO(Comments comment, Long postId, String currentUser){

        boolean isOwner = comment.getUser().getUsername().equals(currentUser);



        return commentMapper.toResponseDTO(comment, postId, currentUser,isOwner);
    }



}
