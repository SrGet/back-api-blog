package com.api.blog.Repositories;

import com.api.blog.Model.Post;
import com.api.blog.Model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findAllByUserId(Pageable pageable, Long userId);
    Long countByUser(User user);

    @EntityGraph(attributePaths = "user")
    @Query("SELECT p from Post p")
    Page<Post> findPostsWithUsers(Pageable pageable);






}
