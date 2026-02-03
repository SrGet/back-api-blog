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
            log.info("Comment creating successful. Returning DTO");
            return getCommentDTO(commentCreated, post.getId(),currentUsername);
        } catch (Exception e) {
            log.error("Couldn't save post. Deleting file. Reason: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public CommentResponse getCommentDTO(Comments comment, Long postId, String currentUser){
        String username = comment.getUser().getUsername();
        boolean isOwner = username.equals(currentUser);

        log.info("CommentUser: {} --- CurrentUser: {}",username, currentUser );

        return commentMapper.toResponseDTO(comment, postId, username,isOwner);
    }

    public Page<CommentResponse> getPostComments(int pageNo, int pageSize, Long postId, String username){

        Page<Comments> commentsList = commentRepository.findAllByPostId(PageRequest.of(pageNo,pageSize), postId);
        if(commentsList != null){
            return commentsList.map(comment -> getCommentDTO(comment, postId, username));
        }else {
            return null;
        }

    }



}
