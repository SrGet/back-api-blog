package com.api.blog.Controllers;

import com.api.blog.DTOs.JwtResponse;
import com.api.blog.DTOs.LoginRequestDTO;
import com.api.blog.DTOs.UserDto;
import com.api.blog.Model.RefreshToken;
import com.api.blog.Model.User;
import com.api.blog.Repositories.RefreshTokenRepository;
import com.api.blog.Service.AuthService;
import com.api.blog.Service.BlackListService;
import com.api.blog.Service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final BlackListService blackListService;
    private final AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody @Valid LoginRequestDTO loginRequest){

        JwtResponse jwt = authService.login(loginRequest);
        RefreshToken refreshToken = authService.getRefreshToken(loginRequest.getUsername());
        ResponseCookie responseCookie = ResponseCookie.from("refreshToken", refreshToken.getToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("Lax")
                .build();

        return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.SET_COOKIE, responseCookie.toString()).body(jwt);

    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid UserDto registerRequest){
        authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        blackListService.addToBlackList(request);
        authService.deleteRefreshToken(request);
        return ResponseEntity.ok("Logout test successful");
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(@CookieValue(value = "refreshToken", required = false) String refreshTokenValue) {

        if(refreshTokenValue == null){
            throw  new RuntimeException("Refresh token missing.");
        }

        // Getting new JWT
        String jwt = authService.refreshAccessToken(refreshTokenValue);
        JwtResponse jwtResponse = JwtResponse.builder()
                .jwt(jwt)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(jwtResponse);
    }


}
