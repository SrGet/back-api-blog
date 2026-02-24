package com.api.blog.notifications;

import com.api.blog.Service.NotificationService;
import com.api.blog.notifications.events.LikePostEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class LikePostNotificationListener {

    private final NotificationService notificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handle(LikePostEvent event){
        notificationService.createLikePostNotification(event.likedBy(),event.recipient());

    }
}
