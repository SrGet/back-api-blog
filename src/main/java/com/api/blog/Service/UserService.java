package com.api.blog.Service;

import com.api.blog.DTOs.UserDto;
import com.api.blog.Model.Post;
import com.api.blog.Model.User;
import com.api.blog.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User create(User user){

        try {

            return userRepository.save(user);

        } catch (Exception e) {

            throw new RuntimeException("Error creating new user");
        }

    }

    public User getUserByUsername(String username){
        return userRepository.findByUsername(username);
    }

}
