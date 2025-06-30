package com.sobok.userservice.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@ToString
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "auth_id", nullable = false)
    private Long authId;

    @Column(nullable = false)
    private String nickname;

    private String photo;

    private String email;

    @Column(nullable = false)
    private String phone;
}
