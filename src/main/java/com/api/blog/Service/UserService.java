package com.api.blog.Service;

import com.api.blog.DTOs.UserDto;
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

}
