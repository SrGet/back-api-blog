package com.api.blog.Service;

import com.api.blog.DTOs.FollowResponseDTO;
import com.api.blog.Model.Follows;
import com.api.blog.Model.User;
import com.api.blog.Repositories.FollowRepository;
import com.api.blog.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Transactional
    public FollowResponseDTO toggleFollow(String follower, String followTarget ){

        User currentUser = userRepository.findByUsername(follower);
        User followedUser = userRepository.findByUsername(followTarget);

        if(currentUser.getId().equals(followedUser.getId())){
            throw  new IllegalArgumentException("Cannot follow yourself :/");
        }

        log.info("Executing existByFollowerAndFollowed");
        if (followRepository.existsByFollowerAndFollowed(currentUser,followedUser)){

            followRepository.deleteByFollowerAndFollowed(currentUser,followedUser);
            log.info("Deleting follow successful, follower: {}, followed: {}", currentUser.getUsername(), followedUser.getUsername());
            return FollowResponseDTO.builder()
                    .followed(false)
                    .build();
        }

        Follows followerFollowed = Follows.builder()
                .follower(currentUser)
                .followed(followedUser)
                .build();

        followRepository.save(followerFollowed);

        log.info("Creating follow successful, follower: {}, followed: {}", currentUser.getUsername(), followedUser.getUsername());
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
