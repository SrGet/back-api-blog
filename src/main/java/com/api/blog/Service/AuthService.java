package com.api.blog.Service;

import com.api.blog.DTOs.JwtResponse;
import com.api.blog.DTOs.LoginRequestDTO;
import com.api.blog.DTOs.UserDto;
import com.api.blog.ErrorHandling.customExceptions.ResourceNotFoundException;
import com.api.blog.Model.RefreshToken;
import com.api.blog.Model.Roles;
import com.api.blog.Model.User;
import com.api.blog.Repositories.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;


    public JwtResponse login(LoginRequestDTO loginRequest){

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword()));
            log.info("Authentication successful for user {}", loginRequest.getUsername());

        } catch (Exception e) {

            log.info("Login failed for user {}. Reason: {}",loginRequest.getUsername(),e.getMessage());
            throw new RuntimeException(e);
        }

        UserDetails user = userService.getUserByUsername(loginRequest.getUsername());
        String jwt = jwtService.getToken(user);
        return JwtResponse.builder()
                .jwt(jwt)
                .build();
    }


    public void register(UserDto registerRequest){

        User user = User.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .email(registerRequest.getEmail())
                .role(Roles.USER)
                .active(true)
                .profileImgKey(registerRequest.getProfileImgUrl())
                .build();

        userService.create(user);

    }

    public RefreshToken getRefreshToken(String username){

        User user = (User) userService.getUserByUsername(username);
        RefreshToken currentRefreshToken = refreshTokenRepository.findByUser(user);
        if(currentRefreshToken != null && currentRefreshToken.getExpireDate().isAfter(Instant.now())){
            return currentRefreshToken;
        }

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expireDate(Instant.now().plus(7, ChronoUnit.DAYS))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public String refreshAccessToken(String refreshTokenValue){

        // Validating refresh token
        RefreshToken token = refreshTokenRepository.findByToken(refreshTokenValue).orElseThrow(
                () -> new ResourceNotFoundException("Token not found"));
        if (token.getExpireDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token expired");
        }
        // Getting new access token
        User user = token.getUser();
        return jwtService.getToken(user);


    }
    @Transactional
    public void deleteRefreshToken(HttpServletRequest request){
        String token = jwtService.getTokenFromRequest(request);
        String username = jwtService.getUsernameFromToken(token);
        User user = (User) userService.getUserByUsername(username);
        refreshTokenRepository.deleteByUser(user);

    }

}
