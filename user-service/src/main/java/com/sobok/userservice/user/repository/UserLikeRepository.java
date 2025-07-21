package com.sobok.userservice.user.repository;

import com.sobok.userservice.user.dto.response.PostLikeCount;
import com.sobok.userservice.user.entity.UserLike;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;
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

    /**
     * 모든 게시글(post_id)의 좋아요 수를 조회하여 Map 형태로 반환
     */
    @Query(value = "SELECT u.post_id as postId, COUNT(*) as count FROM user_like u GROUP BY u.post_id", nativeQuery = true)
    List<PostLikeCount> countLikesGroupedByPostId();

    /**
     * 좋아요 수 기준으로 게시글을 정렬하여 페이징된 결과를 가져옴
     */
    @Query(value = """
                SELECT u.post_id AS postId, COUNT(*) AS count
                FROM user_like u
                GROUP BY u.post_id
                ORDER BY count DESC
                LIMIT :size OFFSET :offset
            """, nativeQuery = true)
    List<PostLikeCount> findMostLikedPosts(@Param("offset") int offset, @Param("size") int size);

    /**
     * 좋아요가 눌린 게시글의 post_id 수를 카운트 (페이징 계산용 )
     */
    @Query(value = """
                SELECT COUNT(DISTINCT u.post_id)
                FROM user_like u
            """, nativeQuery = true)
    Long countDistinctPostId();

    /**
     * 여러 게시글의 ID를 받아서 각 게시글에 눌린 좋아요 수를 알려줌
     */
    @Query(value = """
    SELECT u.post_id AS postId, COUNT(*) AS count
    FROM user_like u
    WHERE u.post_id IN (:postIds)
    GROUP BY u.post_id
""", nativeQuery = true)
    List<PostLikeCount> countLikesByPostIds(@Param("postIds") List<Long> postIds);

}
