package com.api.blog.Controllers;

import com.api.blog.DTOs.EditPostDTO;
import com.api.blog.DTOs.NewPostDto;
import com.api.blog.DTOs.PostResponseDTO;
import com.api.blog.Service.LikeService;
import com.api.blog.Service.PostService;
import lombok.RequiredArgsConstructor;
import org.apache.http.protocol.HTTP;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final LikeService likeService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<PostResponseDTO> create (@ModelAttribute NewPostDto newPostDto){

        PostResponseDTO post = postService.create(newPostDto);

        URI location =  ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(post.getId())
                .toUri();

        return ResponseEntity.created(location).body(post);

    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<PostResponseDTO> getPost (@PathVariable("id") Long postId){

        return ResponseEntity.ok(postService.getPostDTO(postId));

    }

    @GetMapping("/myPosts")
    public ResponseEntity<List<PostResponseDTO>> myPosts(){
        return ResponseEntity.ok(postService.getUserPosts());

    }

    @DeleteMapping("/delete/{postId}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<String> delete (@PathVariable Long postId){

        return ResponseEntity.ok( postService.delete(postId));
    }

    @PatchMapping("/update")
    public ResponseEntity<PostResponseDTO> update(@RequestBody EditPostDTO editPostDTO){
        return ResponseEntity.ok(postService.update(editPostDTO));
    }

    @PatchMapping("/like")
    public ResponseEntity<String> likePost(@RequestParam Long postId){
        return ResponseEntity.ok(likeService.likePost(postId));
    }


}
