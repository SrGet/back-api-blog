package com.api.blog.Controllers;

import com.api.blog.DTOs.PostResponseDTO;
import com.api.blog.Service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feed")
public class FeedController {

    private final FeedService feedService;

    @GetMapping("/get")
    public ResponseEntity<Page<PostResponseDTO>> get(@RequestParam int pageNo,
                                                     @RequestParam int pageSize){

        return ResponseEntity.ok(feedService.getUserFeed(pageNo, pageSize));


    }
}
