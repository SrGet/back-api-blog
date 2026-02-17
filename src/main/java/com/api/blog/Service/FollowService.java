package com.api.blog.Service;

import com.api.blog.ErrorHandling.customExceptions.ResourceNotFoundException;
import com.api.blog.DTOs.FollowResponseDTO;
import com.api.blog.Model.Follows;
import com.api.blog.Model.User;
import com.api.blog.Repositories.FollowRepository;
import com.api.blog.Repositories.UserRepository;
import com.api.blog.notifications.events.FollowEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final StringRedisTemplate redisTemplate;

    @Transactional
    public FollowResponseDTO toggleFollow(String follower, String followTarget ){
        log.info("Thread MethodToggleFollow: {}", Thread.currentThread().getName());

        User currentUser = userRepository.findByUsername(follower).orElseThrow(
                () -> new ResourceNotFoundException("Couldn't find user: " + follower));

        User followedUser = userRepository.findByUsername(followTarget).orElseThrow(
                () -> new ResourceNotFoundException("Couldn't find user: " + followTarget));

        if(currentUser.getId().equals(followedUser.getId())){
            throw  new IllegalArgumentException("Cannot follow yourself :/");
        }

        log.info("Executing existByFollowerAndFollowed");
        if (followRepository.existsByFollowerAndFollowed(currentUser,followedUser)){

            followRepository.deleteByFollowerAndFollowed(currentUser,followedUser);

            Long followersCount = redisTemplate.opsForValue().decrement("followers:amount:" + followedUser.getId());
            redisTemplate.opsForValue().decrement("followed:amount:" + currentUser.getId());

            return FollowResponseDTO.builder()
                    .followed(false)
                    .followersCount(followersCount)
                    .build();
        }

        Follows followerFollowed = Follows.builder()
                .follower(currentUser)
                .followed(followedUser)
                .build();

        followRepository.save(followerFollowed);

        //  *** Notification event ***
        applicationEventPublisher.publishEvent(new FollowEvent(currentUser, followedUser));


        Long followersCount = redisTemplate.opsForValue().increment("followers:amount:" + followedUser.getId());
        redisTemplate.opsForValue().increment("following:amount:" + currentUser.getId());

        return FollowResponseDTO.builder()
                .followed(true)
                .followersCount(followersCount)
                .build();

    }

    public Long getFollowingCount(User follower){
        String count = redisTemplate.opsForValue().get("following:amount:" + follower.getId());
        log.info("FollowingCount: {}",count);
        if (count != null){
            return Long.parseLong(count);
        }
        Long countDb = followRepository.countByFollower(follower);
        redisTemplate.opsForValue().set("following:amount:" + follower.getId(), countDb.toString());
        return countDb;
    }

    public Long getFollowersCount(User followed){
        String count = redisTemplate.opsForValue().get("followers:amount:" + followed.getId());
        log.info("FollowersCount: {}",count);
        if (count != null){
            return Long.parseLong(count);
        }

        Long countDb = followRepository.countByFollowed(followed);
        redisTemplate.opsForValue().set("followers:amount:" + followed.getId(), countDb.toString());
        return countDb;
    }

    public boolean isFollowed(User currentUser, User targetUser){
        return followRepository.existsByFollowerAndFollowed(currentUser,targetUser);
    }



}
