package com.api.blog.Service;

import com.api.blog.DTOs.NotificationResponse;
import com.api.blog.Mappers.NotificationMapper;
import com.api.blog.Model.Notification;
import com.api.blog.Model.User;
import com.api.blog.Repositories.NotificationRepository;
import com.api.blog.Repositories.UserRepository;
import com.api.blog.Utils.RedisKeys;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final StringRedisTemplate redisTemplate;

    public void createFollowNotification(User followed, User follower){

        Notification notification = Notification.builder()
                .sender(follower)
                .recipient(followed)
                .message(follower.getUsername() + " is following you.")
                .build();
        notificationRepository.save(notification);


        redisTemplate.opsForValue().increment(RedisKeys.notificationsUnread(followed.getId()));

    }

    public void createLikePostNotification(User likedBy, User recipient){

        Notification notification = Notification.builder()
                .sender(likedBy)
                .recipient(recipient)
                .message(likedBy.getUsername() + " has liked your post.")
                .alreadyRead(false)
                .build();

        notificationRepository.save(notification);
        redisTemplate.opsForValue().increment(RedisKeys.notificationsUnread(recipient.getId()));

    }

    @Transactional
    public Page<NotificationResponse> getNotifications(String currentUser, Pageable pageable){

        User current = userRepository.findByUsername(currentUser).orElseThrow(() -> new EntityNotFoundException("User not found"));

        Page<Notification> notifications = notificationRepository.findAllByRecipientOrderByCreatedAtDesc(current, pageable);

        if (notifications.getContent().isEmpty()){
            return Page.empty();
        }


        redisTemplate.delete(RedisKeys.notificationsUnread(current.getId()));

        notificationRepository.setAlreadyReadAsTrueByRecipient(current);

        return notifications.map(notificationMapper::toDto);

    }

    public Long getCount(String currentUser){
        User current = userRepository.findByUsername(currentUser).orElseThrow(() -> new EntityNotFoundException("User not found"));

        String stringCount = redisTemplate.opsForValue().get(RedisKeys.notificationsUnread(current.getId()));
        if(stringCount != null){
            return Long.parseLong(stringCount);
        }
        Long countDb = notificationRepository.countByRecipientAndAlreadyReadFalse(current);
        redisTemplate.opsForValue().set(RedisKeys.notificationsUnread(current.getId()), countDb.toString());
        return countDb;
    }
}
