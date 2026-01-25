package com.api.blog.Service;

import com.api.blog.DTOs.PostResponseDTO;
import com.api.blog.DTOs.UserDto;
import com.api.blog.DTOs.UserProfileDTO;
import com.api.blog.Mappers.PostMapper;
import com.api.blog.Model.Post;
import com.api.blog.Model.User;
import com.api.blog.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public User getUserByUsername(String username){
        return userRepository.findByUsername(username);
    }

    public UserProfileDTO getProfile(String currentUser,String username){

        User authuser = getUserByUsername(currentUser);
        User user = getUserByUsername(username);

        Long followingAmount = followService.getFollowingCount(user);
        Long followersAmount = followService.getFollowersCount(user);

        boolean followed =  followService.isFollowed(authuser,user);

        return UserProfileDTO.builder()

                .profileImgUrl(user.getProfileImgKey() != null ? "/file/"+user.getProfileImgKey() : null)
                .username(user.getUsername())
                .following(followingAmount)
                .followers(followersAmount)
                .followed(followed)
                .build();




    }




}
