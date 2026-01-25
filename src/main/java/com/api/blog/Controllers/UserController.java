package com.api.blog.Controllers;
import com.api.blog.DTOs.UserProfileDTO;
import com.api.blog.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/user")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;



    @GetMapping("/hi")
    public ResponseEntity<String> hi(){
        System.out.println("Entra al endpoint secure");
        return ResponseEntity.ok("Hi from test-secure endpoint");
    }


    @GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> getProfile(Principal principal,@RequestParam String username){

        return ResponseEntity.ok(userService.getProfile(principal.getName(), username));

    }

}
