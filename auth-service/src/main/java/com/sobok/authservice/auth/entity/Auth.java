package com.sobok.authservice.auth.entity;

import com.sobok.authservice.common.entity.BaseTimeEntity;
import com.sobok.authservice.common.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "auth")
public class Auth extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login_id", nullable = false, unique = true)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.USER;

    @Builder.Default
    @Column(nullable = false)
    private String active = "Y";

    public void changeActive() {
        this.active = this.active.equals("Y") ? "N" : "Y";
    }

    public void changeActive(boolean newActive) {
        this.active = newActive ? "Y" : "N";
    }

     public void changePassword(String newPassword) {
        this.password = newPassword;
    }
}