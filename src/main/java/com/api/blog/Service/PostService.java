package com.api.blog.Service;

import com.api.blog.DTOs.EditPostDTO;
import com.api.blog.DTOs.NewPostDto;
import com.api.blog.DTOs.PostResponseDTO;
import com.api.blog.Mappers.PostMapper;
import com.api.blog.Model.Post;
import com.api.blog.Model.User;
import com.api.blog.Repositories.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final MinIOService minIOService;
    private final UserService userService;
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final LikeService likeService;

    // Create Post
    @Transactional
    public PostResponseDTO create(NewPostDto newPost){

        User user = userService.getUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

        String keyFile = minIOService.uploadFile(newPost.getFile());

        try {

            Post post = Post.builder()
                    .message(newPost.getMessage())
                    .imageUrl(keyFile)
                    .user(user)
                    .build();

           Post postCreated = postRepository.save(post);
            log.info("Post creation successful. Returning DTO");
            return getPostDTO(postCreated.getId());

        } catch (Exception e) {

            log.error("Post creation failed, deleting file. Reason: {}",e.getMessage());
            minIOService.deleteFile(keyFile);

            throw new RuntimeException("Error creating post: " + e.getMessage());
        }

    }

    // Get single postDTO by ID
    public PostResponseDTO getPostDTO(Long idPost){
        User user = userService.getUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        Post post = postRepository.findById(idPost).orElseThrow(() -> new NoSuchElementException("Couldn't find post with id: " + idPost));
        boolean isLiked = likeService.isLiked(user,post);
        return postMapper.toResponseDto(post,isLiked);
    }

    // Get single PostEntity by ID
    public Post getPostEntity(Long idPost){
        return postRepository.findById(idPost).orElseThrow(()
                -> new NoSuchElementException("Couldn't find post with id: " + idPost));
    }


     // Delete user post
    public String delete (Long postId){

        Post post = getPostEntity(postId);

        if(!post.getUser().getUsername().equals(SecurityContextHolder.getContext().getAuthentication().getName())){
            throw new AccessDeniedException("You are not allowed to delete this post.");
        }

        postRepository.deleteById(postId);

        if(post.getImageUrl() != null){
            minIOService.deleteFile(post.getImageUrl());
        }

        return "Post deleted successfully";

    }

    // Update Post
    public PostResponseDTO update(EditPostDTO editPostDTO){
        User user = userService.getUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

        Post oldPost = getPostEntity(editPostDTO.getPostId());

        if(!oldPost.getUser().getUsername().equals(user.getUsername())){
            throw new AccessDeniedException("You are not allowed to delete this post.");
        }

        if(!editPostDTO.getNewMessage().isBlank()){
            oldPost.setMessage(editPostDTO.getNewMessage());
        }
        Post editedPost = postRepository.save(oldPost);
        return getPostDTO(editedPost.getId());

    }

    // Get LastsPosts
    public List<Post> getLastsPosts(Pageable pageable){
        return postRepository.findAll(pageable).getContent();

    }

}
