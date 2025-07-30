package com.api.blog.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/paladin")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<String> hi(){
        System.out.println("Entra al endpoint secure");
        return ResponseEntity.ok("Hi from test-secure endpoint");
    }


}
