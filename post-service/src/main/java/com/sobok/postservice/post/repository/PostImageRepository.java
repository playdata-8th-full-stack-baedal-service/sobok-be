package com.sobok.postservice.post.repository;

import com.sobok.postservice.post.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    void deleteByPostId(Long postId);

    Optional<PostImage> findTopByPostIdOrderByIndexAsc(Long postId);

    List<PostImage> findAllByPostId(Long postId);

    List<PostImage> findAllByPostIdInAndIndex(List<Long> postIds, Integer index);
}
