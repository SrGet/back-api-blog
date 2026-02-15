package com.api.blog.Repositories;

import com.api.blog.Model.Notification;
import com.api.blog.Model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,Long> {

    Page<Notification> findAllByRecipientOrderByCreatedAtDesc(User recipient, Pageable pageable);
}
