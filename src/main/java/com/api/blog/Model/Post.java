package com.api.blog.Model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "posts")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    @Column(name = "image_url")
    private String imageUrl;

    @OneToMany(mappedBy = "post")
    private List<PostLike> likes = new ArrayList<>();

    @Column(nullable = false)
    private boolean active;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

}
