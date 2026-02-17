package com.api.blog.Controllers;

import com.api.blog.DTOs.EditPostDTO;
import com.api.blog.DTOs.LikeResponseDTO;
import com.api.blog.DTOs.NewPostDto;
import com.api.blog.DTOs.PostResponseDTO;
import com.api.blog.Service.LikeService;
import com.api.blog.Service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.security.Principal;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final LikeService likeService;

    @PostMapping("/create")
    public ResponseEntity<PostResponseDTO> create (@ModelAttribute NewPostDto newPostDto, Principal principal){

        PostResponseDTO post = postService.create(newPostDto, principal.getName());

        URI location =  ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(post.getId())
                .toUri();

        return ResponseEntity.created(location).body(post);
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<Void> delete (@PathVariable Long postId, Principal principal){
        postService.delete(postId, principal.getName());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/update")
    public ResponseEntity<PostResponseDTO> update(@RequestBody EditPostDTO editPostDTO, Principal principal){
        return ResponseEntity.ok(postService.update(editPostDTO, principal.getName()));
    }

    @PatchMapping("/like/{postId}")
    public ResponseEntity<LikeResponseDTO> toggleLike(@PathVariable Long postId, Principal principal){
        return ResponseEntity.ok(likeService.togglePostLike(postId, principal.getName()));
    }

}
