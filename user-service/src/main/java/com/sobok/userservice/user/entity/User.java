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

    @Column(unique = true, nullable = false)
    private Long authId;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Setter
    @Column(unique = true, nullable = true)
    private String email;

    @Column(nullable = false)
    private String photo;

    @Column(unique = true, nullable = false)
    private String phone;

}
