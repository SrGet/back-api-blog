package com.api.blog.Controllers;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/hi")
    public ResponseEntity<String> hi(){
        System.out.println("Entra al endpoint secure");
        return ResponseEntity.ok("Hi from test-secure endpoint");
    }


}
