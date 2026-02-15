package com.api.blog.Controllers;

import com.api.blog.DTOs.NotificationResponse;
import com.api.blog.Model.Notification;
import com.api.blog.Service.NotificacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificacionService notificacionService;


    @GetMapping("/get")
    public ResponseEntity<Page<NotificationResponse>> get(Principal principal,
                                                          @RequestParam int pageNo,
                                                          @RequestParam int pageSize){

        return ResponseEntity.ok(notificacionService.getNotifications(principal.getName(), PageRequest.of(pageNo,pageSize)));

    }

    @GetMapping("/count")
    public ResponseEntity<Long> getNotificationsCount(Principal principal){

        return ResponseEntity.status(HttpStatus.OK).body(notificacionService.getCount(principal.getName()));


    }


}
