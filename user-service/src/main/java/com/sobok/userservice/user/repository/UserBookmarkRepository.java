package com.sobok.userservice.user.repository;

import com.sobok.userservice.user.dto.response.UserBookmarkResDto;
import com.sobok.userservice.user.entity.UserBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserBookmarkRepository extends JpaRepository<UserBookmark, Long> {
    boolean existsByUserIdAndCookId(Long userId, Long cookId);
    UserBookmark findByUserIdAndCookId(Long userId, Long cookId);
    List<UserBookmark> findByUserId(Long userId);
}
