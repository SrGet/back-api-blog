package com.api.blog.Service;

import com.api.blog.DTOs.UserDto;
import com.api.blog.Model.User;
import com.api.blog.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final KeycloakService keycloakService;
    private final UserRepository userRepository;


    public User create(UserDto userDto){

        String userId = keycloakService.create(userDto);

        if(userId == null){
            throw new IllegalStateException("Could not create user on Keycloak");
        }

        try {
            User user = User.builder()

                    .username(userDto.getUsername())
                    .name(userDto.getName())
                    .lastname(userDto.getLastName())
                    .age(userDto.getAge())
                    .email(userDto.getEmail())
                    .active(true)
                    .build();

            return userRepository.save(user);
        } catch (Exception e) {
            keycloakService.delete(userId);
            throw new RuntimeException("Error creating new user");
        }


    }


}
