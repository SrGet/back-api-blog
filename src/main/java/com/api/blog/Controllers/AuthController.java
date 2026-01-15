package com.api.blog.Controllers;

import com.api.blog.DTOs.JwtResponse;
import com.api.blog.DTOs.LoginRequestDTO;
import com.api.blog.DTOs.UserDto;
import com.api.blog.Model.User;
import com.api.blog.Service.AuthService;
import com.api.blog.Service.BlackListService;
import com.api.blog.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    //private final UserService userService;
    private final BlackListService blackListService;
    private final AuthService authService;



    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequestDTO loginRequest){
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody UserDto registerRequest){
        authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /*
    @PostMapping("/register")
    public ResponseEntity<User> create (@RequestBody UserDto userDto){

        return ResponseEntity.ok(userService.create(userDto));
    }

    **/
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        blackListService.addToBlackList(request);

        return ResponseEntity.ok("Logout test successful");
    }
}
