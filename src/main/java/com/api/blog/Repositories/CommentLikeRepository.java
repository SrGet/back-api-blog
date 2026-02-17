package com.api.blog.Repositories;

import com.api.blog.Model.CommentLike;
import com.api.blog.Model.Comments;
import com.api.blog.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    boolean existsByUserAndComments(User user, Comments comment);

    void deleteByUserAndComments(User user, Comments comment);

    Long countByComments(Comments comment);
}
