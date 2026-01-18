package com.api.blog.Controllers;

import com.api.blog.DTOs.EditPostDTO;
import com.api.blog.DTOs.LikeResponseDTO;
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
import java.util.Map;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final LikeService likeService;

    @PostMapping("/create")
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
    public ResponseEntity<PostResponseDTO> getPost (@PathVariable("id") Long postId){

        return ResponseEntity.ok(postService.getPostDTO(postId));

    }


    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<String> delete (@PathVariable Long postId){

        return ResponseEntity.ok( postService.delete(postId));
    }

    @PatchMapping("/update")
    public ResponseEntity<PostResponseDTO> update(@RequestBody EditPostDTO editPostDTO){
        return ResponseEntity.ok(postService.update(editPostDTO));
    }

    @PatchMapping("/like")
    public ResponseEntity<LikeResponseDTO> toggleLike(@RequestParam Long postId){
        return ResponseEntity.ok(likeService.toggleLike(postId));
    }




}
