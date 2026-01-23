package com.api.blog.Repositories;

import com.api.blog.Model.Follows;
import com.api.blog.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<Follows, Long> {

    boolean existsByFollowerAndFollowed(User follower, User followed);

    void deleteByFollowerAndFollowed(User follower, User followed);

    long countByFollowed(User followed);

    long countByFollower(User follower);
}
