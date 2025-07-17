package com.sobok.userservice.user.repository;

import com.sobok.userservice.user.dto.response.PostLikeCount;
import com.sobok.userservice.user.entity.UserLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserLikeRepository extends JpaRepository<UserLike, Long> {

    /**
     * 사용자와 게시글의 좋아요 정보를 조회
     */
    Optional<UserLike> findByUserIdAndPostId(Long userId, Long postId);

    /**
     * 사용자가 좋아요한 모든 게시글 정보를 페이징 처리하여 조회
     */
    Page<UserLike> findAllByUserId(Long userId, Pageable pageable);

    /**
     * 게시글에 눌린 전체 좋아요 수 전달
     */
    Long countByPostId(Long postId);

    @Query(value = "SELECT u.post_id as postId, COUNT(*) as count FROM user_like u GROUP BY u.post_id", nativeQuery = true)
    List<PostLikeCount> countLikesGroupedByPostId();
}
