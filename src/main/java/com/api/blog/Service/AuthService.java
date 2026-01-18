package com.api.blog.Service;

import com.api.blog.DTOs.JwtResponse;
import com.api.blog.DTOs.LoginRequestDTO;
import com.api.blog.DTOs.UserDto;
import com.api.blog.Model.Roles;
import com.api.blog.Model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final JwtService jwtService;



    public JwtResponse login(LoginRequestDTO loginRequest){

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword()));

        UserDetails user = userService.getUserByUsername(loginRequest.getUsername());

        String jwt = jwtService.getToken(user);
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
