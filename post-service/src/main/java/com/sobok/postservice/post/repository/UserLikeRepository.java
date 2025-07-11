package com.sobok.postservice.post.repository;

import com.sobok.postservice.post.entity.UserLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLikeRepository extends JpaRepository<UserLike, Long> {
    int countByPostId(Long postId); // 좋아요 수
}
