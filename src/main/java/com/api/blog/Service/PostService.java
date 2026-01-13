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
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PostService {

    private final MinIOService minIOService;
    private final UserService userService;
    private final PostRepository postRepository;
    private final PostMapper postMapper;

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
                    .active(true)
                    .build();

            postRepository.save(post);

            return postMapper.toResponseDto(post);

        } catch (Exception e) {

            minIOService.deleteFile(keyFile);

            throw new RuntimeException("Error creating post: " + e.getMessage());
        }

    }

    // Get single postDTO by ID
    public PostResponseDTO getPostDTO(Long idPost){
        return postMapper.toResponseDto(postRepository.findById(idPost).orElseThrow(()
                -> new NoSuchElementException("Couldn't find post with id: " + idPost)));
    }

    // Get single PostEntity by ID
    public Post getPostEntity(Long idPost){
        return postRepository.findById(idPost).orElseThrow(()
                -> new NoSuchElementException("Couldn't find post with id: " + idPost));
    }

    // Get user posts
    public List<PostResponseDTO> getUserPosts(){
        List<Post> posts = userService.myPosts();
        return posts.stream().map(postMapper::toResponseDto).toList();
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

        Post oldPost = getPostEntity(editPostDTO.getPostId());

        if(!oldPost.getUser().getUsername().equals(SecurityContextHolder.getContext().getAuthentication().getName())){
            throw new AccessDeniedException("You are not allowed to delete this post.");
        }

        if(!editPostDTO.getNewMessage().isBlank()){
            oldPost.setMessage(editPostDTO.getNewMessage());
        }
        postRepository.save(oldPost);
        return postMapper.toResponseDto(oldPost);

    }

    // Get LastsPosts
    public List<Post> getLastsPosts(Pageable pageable){

        return postRepository.findAll(pageable).getContent();

    }


}
