package com.api.blog.notifications;

import com.api.blog.Service.NotificacionService;
import com.api.blog.notifications.events.FollowEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FollowNotificationListener {

    private final NotificacionService notificacionService;

    @EventListener
    @Async
    public void handle(FollowEvent event){
        notificacionService.createFollowNotification(event.followed(), event.follower());

    }

}
