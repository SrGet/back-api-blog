package com.api.blog.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {


    public ResponseEntity<String> hi(){
        return ResponseEntity.ok("Hi from test-secure endpoint");
    }
}
