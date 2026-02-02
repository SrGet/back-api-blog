package com.api.blog.Repositories;

import com.api.blog.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

   User findByUsername(String username);
}
