package com.api.blog.Repositories;

import com.api.blog.Model.RefreshToken;
import com.api.blog.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String refreshTokenValue);
    RefreshToken findByUser(User user);
    void deleteByUser(User user);
}
