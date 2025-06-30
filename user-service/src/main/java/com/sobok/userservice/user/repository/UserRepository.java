package com.sobok.userservice.user.repository;

import com.sobok.userservice.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Long> {
}
