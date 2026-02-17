package com.api.blog.Controllers;

import com.api.blog.DTOs.LikeResponseDTO;
import com.api.blog.DTOs.NewCommentRequest;
import com.api.blog.DTOs.CommentResponse;
import com.api.blog.Service.CommentService;
import com.api.blog.Service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final LikeService likeService;

    @PostMapping("/create")
    public ResponseEntity<CommentResponse> create(Principal principal, @ModelAttribute NewCommentRequest newCommentRequest){

        CommentResponse comment = commentService.create(principal.getName(),newCommentRequest);

        URI location =  ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(comment.getId())
                .toUri();

        return ResponseEntity.created(location).body(comment);
    }

    @GetMapping("/get")
    public ResponseEntity<Page<CommentResponse>> get(@RequestParam int pageNo,
                                                     @RequestParam int pageSize,
                                                     @RequestParam Long postId,
                                                     Principal principal){
        return ResponseEntity.ok(commentService.getPostComments(pageNo,pageSize,postId, principal.getName()));

    }

    @PatchMapping("/delete/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable Long commentId){
        commentService.delete(commentId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/like/{commentId}")
    public ResponseEntity<LikeResponseDTO> toggleLike(@PathVariable Long commentId, Principal principal){
        return ResponseEntity.ok(likeService.toggleCommentLike(commentId, principal.getName()));
    }
}
