package com.sobok.authservice.auth.repository;

import com.sobok.authservice.auth.entity.Auth;
import com.sobok.authservice.common.dto.ApiResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.User;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<Auth, Long> {
    Optional<Auth> findByLoginId(String loginId);
    Optional<Auth> findByIdAndActive(Long authId, String active);
    Optional<Auth> findByLoginIdAndActive(String loginId, String active);
    Optional<Auth> findByOauthId(Long id);
}
