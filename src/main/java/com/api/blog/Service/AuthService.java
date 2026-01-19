package com.api.blog.Service;

import com.api.blog.DTOs.JwtResponse;
import com.api.blog.DTOs.LoginRequestDTO;
import com.api.blog.DTOs.UserDto;
import com.api.blog.Model.Roles;
import com.api.blog.Model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final JwtService jwtService;


    public JwtResponse login(LoginRequestDTO loginRequest){

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword()));
            log.info("Authentication successful for user {}", loginRequest.getUsername());

        } catch (Exception e) {

            log.info("Login failed for user {}. Reason: {}",loginRequest.getUsername(),e.getMessage());
            throw new RuntimeException(e);
        }

        UserDetails user = userService.getUserByUsername(loginRequest.getUsername());

        String jwt = jwtService.getToken(user);

        log.info("Login successful for user {}. Generating JWT", loginRequest.getUsername());
        return JwtResponse.builder()
                .jwt(jwt)
                .build();
    }


    public void register(UserDto registerRequest){

        User user = User.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .email(registerRequest.getEmail())
                .role(Roles.USER)
                .active(true)
                .build();

        userService.create(user);

    }

}
