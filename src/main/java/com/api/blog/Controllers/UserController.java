package com.api.blog.Controllers;

import com.api.blog.DTOs.UserDto;
import com.api.blog.Model.User;
import com.api.blog.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {



    @GetMapping("/hi")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<String> hi(){
        return ResponseEntity.ok("Hi from test-secure endpoint");
    }


}
