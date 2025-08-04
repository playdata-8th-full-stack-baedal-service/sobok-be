package com.sobok.authservice.auth.repository;

import com.sobok.authservice.auth.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AuthRepository extends JpaRepository<Auth, Long> {
    Optional<Auth> findByLoginId(String loginId);
    Optional<Auth> findByIdAndActive(Long authId, String active);
    Optional<Auth> findByLoginIdAndActive(String loginId, String active);
    Optional<Auth> findByOauthId(Long id);

    @Query("SELECT a FROM Auth a WHERE a.role = 'RIDER' AND a.active = 'N'")
    List<Auth> findInactiveRiders();
}
