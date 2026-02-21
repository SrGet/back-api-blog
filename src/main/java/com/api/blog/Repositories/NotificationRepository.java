package com.api.blog.Repositories;

import com.api.blog.Model.Notification;
import com.api.blog.Model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,Long> {

    @EntityGraph(attributePaths = "sender")
    Page<Notification> findAllByRecipientOrderByCreatedAtDesc(User recipient, Pageable pageable);

    Long countByRecipientAndAlreadyReadFalse(User recipient);

    @Modifying
    @Query("UPDATE Notification n SET n.alreadyRead = true WHERE n.recipient = :user")
    void setAlreadyReadAsTrueByRecipient(User user);
}
