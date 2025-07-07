package com.api.blog.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/hi")
    public ResponseEntity<String> hi (){
        return ResponseEntity.ok("Hi from test-free endpoint");
    }
}
