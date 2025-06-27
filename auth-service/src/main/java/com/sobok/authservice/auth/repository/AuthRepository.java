package com.sobok.authservice.auth.repository;

import com.sobok.authservice.auth.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuthRepository extends JpaRepository<Auth, Long> {
    Optional<Auth> findByLoginId(String loginId);
}
