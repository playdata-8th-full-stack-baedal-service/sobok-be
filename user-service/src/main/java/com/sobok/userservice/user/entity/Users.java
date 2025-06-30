package com.sobok.userservice.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Long authId;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(unique = true, nullable = true)
    private String email;

    @Column(nullable = false)
    private String photo;

    @Column(unique = true, nullable = false)
    private String phone;
}
