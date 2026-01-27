package com.api.blog.Repositories;

import com.api.blog.Model.Comments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comments, Long> {

    Page<Comments> findAllByPostId(Pageable pageable, Long postId);
}
