package com.sobok.postservice.post.repository;

import com.sobok.postservice.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    // 사용자별 게시글 조회
    Page<Post> findAllByUserId(Long userId, Pageable pageable);

    // 요리별 게시글 목록 조회
    List<Post> findByCookId(Long cookId);

    // 게시글 중복 등록 방지
    boolean existsByPaymentIdAndCookId(Long paymentId, Long cookId);

}
