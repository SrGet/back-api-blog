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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

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
            Long followersCount = followRepository.countByFollowed(followedUser);
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

        applicationEventPublisher.publishEvent(new FollowEvent(currentUser, followedUser));

        Long followersCount = followRepository.countByFollowed(followedUser);

        return FollowResponseDTO.builder()
                .followed(true)
                .followersCount(followersCount)
                .build();

    }


    public Long getFollowingCount(User follower){
        return followRepository.countByFollower(follower);
    }

    public Long getFollowersCount(User follower){
        return followRepository.countByFollowed(follower);
    }

    public boolean isFollowed(User currentUser, User targetUser){
        return followRepository.existsByFollowerAndFollowed(currentUser,targetUser);
    }



}
