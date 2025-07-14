package com.api.blog.Controllers;

import com.api.blog.DTOs.UserDto;
import com.api.blog.Model.User;
import com.api.blog.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/hi")
    public ResponseEntity<String> hi (){
        return ResponseEntity.ok("Hi from test-free endpoint");
    }

    @PostMapping("/register")
    public ResponseEntity<User> create (@RequestBody UserDto userDto){

        return ResponseEntity.ok(userService.create(userDto));
    }
}
