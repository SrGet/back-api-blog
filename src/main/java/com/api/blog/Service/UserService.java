package com.api.blog.Service;

import com.api.blog.ErrorHandling.customExceptions.ResourceNotFoundException;
import com.api.blog.DTOs.UserProfileDTO;
import com.api.blog.Model.User;
import com.api.blog.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final FollowService followService;
    private final PostService postService;

    public User create(User user){

        try {
            log.info("Saving user {}", user.getUsername());
            return userRepository.save(user);

        } catch (Exception e) {
            log.error("Error saving user {}", user.getUsername());
            throw new RuntimeException("Error creating new user");
        }
    }


    public UserProfileDTO getProfile(String currentUser,String username){

        User authuser = userRepository.findByUsername(currentUser).orElseThrow(
                () -> new ResourceNotFoundException("Couldn't find user: " + currentUser));

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new ResourceNotFoundException("Couldn't find user: " + username));

        Long followingAmount = followService.getFollowingCount(user);
        Long followersAmount = followService.getFollowersCount(user);

        boolean followed =  followService.isFollowed(authuser,user);
        Long postsCount = postService.postsCount(user);

        return UserProfileDTO.builder()

                .profileImgUrl(user.getProfileImgKey())
                .username(user.getUsername())
                .following(followingAmount)
                .followers(followersAmount)
                .postsCount(postsCount)
                .followed(followed)
                .build();
    }


    public UserDetails getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
