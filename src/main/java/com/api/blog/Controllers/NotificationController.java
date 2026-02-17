package com.api.blog.Controllers;

import com.api.blog.DTOs.NotificationResponse;
import com.api.blog.Service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;


    @GetMapping("/get")
    public ResponseEntity<Page<NotificationResponse>> get(Principal principal,
                                                          @RequestParam int pageNo,
                                                          @RequestParam int pageSize){

        return ResponseEntity.ok(notificationService.getNotifications(principal.getName(), PageRequest.of(pageNo,pageSize)));

    }

    @GetMapping("/count")
    public ResponseEntity<Long> getNotificationsCount(Principal principal){

        return ResponseEntity.status(HttpStatus.OK).body(notificationService.getCount(principal.getName()));


    }


}
