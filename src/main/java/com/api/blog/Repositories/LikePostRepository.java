package com.api.blog.Repositories;

import com.api.blog.Model.Post;
import com.api.blog.Model.PostLike;
import com.api.blog.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikePostRepository extends JpaRepository<PostLike,Long> {


    boolean existsByUserAndPost(User user, Post post);

    void deleteByUserAndPost(User user, Post post);

    Long countByPost(Post post);

}
