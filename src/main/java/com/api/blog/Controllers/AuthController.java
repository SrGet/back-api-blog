package com.api.blog.Controllers;

import com.api.blog.DTOs.UserDto;
import com.api.blog.Model.User;
import com.api.blog.Service.BlackListService;
import com.api.blog.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final BlackListService blackListService;


    @PostMapping("/register")
    public ResponseEntity<User> create (@RequestBody UserDto userDto){

        return ResponseEntity.ok(userService.create(userDto));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@AuthenticationPrincipal Jwt jwt) {
        blackListService.addToBlackList(jwt);

        return ResponseEntity.ok("Logout test successful");
    }
}
