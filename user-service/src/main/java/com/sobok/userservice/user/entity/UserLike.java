package com.sobok.userservice.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString
@Table(name = "user_like", indexes = {
        @Index(name = "idx_like_userId", columnList = "userId"),
        @Index(name = "idx_like_postId", columnList = "postId")
})
public class UserLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "post_id", nullable = false)
    private Long postId;
}
