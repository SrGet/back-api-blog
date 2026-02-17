package com.api.blog.Controllers;

import com.api.blog.DTOs.FollowResponseDTO;
import com.api.blog.Service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{userTarget}")
    public ResponseEntity<FollowResponseDTO> follow(Principal principal,@PathVariable String userTarget){
        return ResponseEntity.ok(followService.toggleFollow(principal.getName(), userTarget));
    }



}
