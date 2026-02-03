package com.api.blog.Service;

import com.api.blog.DTOs.EditPostDTO;
import com.api.blog.ErrorHandling.customExceptions.ResourceNotFoundException;
import com.api.blog.DTOs.NewPostDto;
import com.api.blog.DTOs.PostResponseDTO;
import com.api.blog.Mappers.PostMapper;
import com.api.blog.Model.Post;
import com.api.blog.Model.User;
import com.api.blog.Repositories.PostRepository;
import com.api.blog.Repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final MinIOService minIOService;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final LikeService likeService;

    // Create Post
    @Transactional
    public PostResponseDTO create(NewPostDto newPost, String username){

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new ResourceNotFoundException("Couldn't find user: " + username));

        String keyFile = minIOService.uploadFile(newPost.getFile());

        try {

            Post post = Post.builder()
                    .message(newPost.getMessage())
                    .imageUrl(keyFile)
                    .deleted_at(null)
                    .user(user)
                    .build();

           Post postCreated = postRepository.save(post);
            log.info("Post creation successful. Returning DTO");
            return getPostDTO(postCreated.getId(), username);

        } catch (Exception e) {

            log.error("Post creation failed, deleting file. Reason: {}",e.getMessage());
            minIOService.deleteFile(keyFile);

            throw new RuntimeException("Error creating post: " + e.getMessage());
        }

    }

    // Get single postDTO by ID
    public PostResponseDTO getPostDTO(Long idPost, String currentUsername){

        User user = userRepository.findByUsername(currentUsername).orElseThrow(
                () -> new ResourceNotFoundException("Couldn't find user: " + currentUsername));

        Post post = postRepository.findById(idPost).orElseThrow(
                () -> new ResourceNotFoundException("Couldn't find post with id: " + idPost));

        boolean owner = user.getUsername().equals(post.getUser().getUsername());
        boolean isLiked = likeService.isLiked(user,post);

        return postMapper.toResponseDto(post,isLiked, owner, user.getProfileImgKey());
    }

    // Get single PostEntity by ID
    public Post getPostEntity(Long idPost){
        return postRepository.findById(idPost).orElseThrow(()
                -> new ResourceNotFoundException("Couldn't find post with id: " + idPost));
    }


     // Delete user post
    public void delete (Long postId){

        Post post = getPostEntity(postId);

        if(!post.getUser().getUsername().equals(SecurityContextHolder.getContext().getAuthentication().getName())){
            throw new AccessDeniedException("You are not allowed to delete this post.");
        }

        if(post.getDeleted_at() != null){
            throw new IllegalArgumentException("Post: " + postId + " is already deleted.");
        }

        post.setDeleted_at(LocalDateTime.now());

        if(post.getImageUrl() != null){
            minIOService.deleteFile(post.getImageUrl());
            post.setImageUrl(null);
        }
        postRepository.save(post);
        log.info("Soft-deleting successful for post: {} ", postId);

    }

    // Update Post
    public PostResponseDTO update(EditPostDTO editPostDTO, String currentUsername){

        User user = userRepository.findByUsername(currentUsername).orElseThrow(
                () -> new ResourceNotFoundException("Couldn't find user: " + currentUsername));

        Post oldPost = getPostEntity(editPostDTO.getPostId());

        if(!oldPost.getUser().getUsername().equals(user.getUsername())){
            throw new AccessDeniedException("You are not allowed to delete this post.");
        }

        if(!editPostDTO.getNewMessage().isBlank()){
            oldPost.setMessage(editPostDTO.getNewMessage());
        }
        Post editedPost = postRepository.save(oldPost);
        return getPostDTO(editedPost.getId(), currentUsername);

    }

    // Get LastsPosts
    public Page<Post> getLastsPosts(Pageable pageable){
        return postRepository.findAll(pageable);

    }

    public Page<Post> getUserPosts(Pageable pageable, String username){
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new ResourceNotFoundException("Couldn't find user: " + username));
        return postRepository.findAllByUserId(pageable, user.getId());

    }

    public Long postsCount(User user){
        return postRepository.countByUser(user);
    }

}
