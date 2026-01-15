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

    //private final KeycloakService keycloakService;
    private final UserRepository userRepository;



    public User create(User user){


        /*


        String userId = keycloakService.create(userDto);
          if(userId == null){
            throw new IllegalStateException("Could not create user on Keycloak");
        }

         */


        try {


            return userRepository.save(user);

        } catch (Exception e) {
            //keycloakService.delete(userId);
            throw new RuntimeException("Error creating new user");
        }


    }

    public User getUserByUsername(String username){
        return userRepository.findByUsername(username);
    }

    public List<Post> myPosts(){
        User user = getUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        return user.getPosts();

    }
}
