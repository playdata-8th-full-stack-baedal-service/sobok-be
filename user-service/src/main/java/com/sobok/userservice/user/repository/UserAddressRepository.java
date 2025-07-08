package com.sobok.userservice.user.repository;

import com.sobok.userservice.user.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
    List<UserAddress> findByUserId(Long userId);

    @Query("SELECT a FROM UserAddress a WHERE a.userId = :userId AND (a.active IS NULL OR a.active = 'Y')")
    List<UserAddress> findByActiveUserId(Long userId);
}
