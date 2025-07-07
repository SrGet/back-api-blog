package com.api.blog.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    public ResponseEntity<String> hi (){
        return ResponseEntity.ok("Hi from test-free endpoint");
    }
}
