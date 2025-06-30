package com.sobok.userservice.user.repository;

import com.sobok.userservice.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByPhone(String phoneNumber);

    Optional<User> findByAuthId(Long authId);

}
