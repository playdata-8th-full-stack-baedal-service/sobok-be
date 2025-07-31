package com.sobok.postservice.post.client;

import com.sobok.postservice.common.config.FeignConfig;
import com.sobok.postservice.post.dto.response.LikedPostPagedResDto;
import com.sobok.postservice.post.dto.response.UserInfoResDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "user-service", url = "${USER_SERVICE_URL}", configuration = FeignConfig.class)
public interface UserFeignClient {

    /**
     * 게시글 작성자 정보(userId, nickname) 조회
     */
    @GetMapping("/api/post-info")
    ResponseEntity<UserInfoResDto> getUserInfo(@RequestParam Long userId);

    /**
     * 게시글의 좋아요 수 조회
     */
    @GetMapping("/api/user-like/count/{postId}")
    ResponseEntity<Long> getLikeCount(@PathVariable Long postId);

    /**
     * 사용자가 좋아요한 게시글 ID 목록 조회 (페이징)
     */
    @GetMapping("/api/user-like/liked-posts")
    ResponseEntity<LikedPostPagedResDto> getLikedPostIds(
            @RequestParam Long userId,
            @RequestParam int page,
            @RequestParam int size
    );

    /**
     * 전체 게시글의 좋아요 수
     */
    @GetMapping("/api/user-like/all-counts")
    ResponseEntity<Map<Long, Long>> getAllLikeCounts();

    /**
     * 좋아요 수 기준으로 게시글 postId 목록을 페이징하여 반환
     */
    @GetMapping("/api/user-like/most-liked")
    ResponseEntity<LikedPostPagedResDto> getMostLikedPostIds(@RequestParam int page, @RequestParam int size);

    /**
     * 사용자 Id 목록을 기반으로 사용자 정보 조회
     */
    @PostMapping("/api/post-info/batch")
    ResponseEntity<Map<Long, UserInfoResDto>> getUserInfos(@RequestBody List<Long> userIds);

    /**
     * 특정 게시글 목록의 좋아요 수를 Map으로 반환
     */
    @PostMapping("/api/user-like/count-map")
    ResponseEntity<Map<Long, Long>> getLikeCountMap(@RequestBody List<Long> postIds);

    /**
     * 게시물 등록할 때 기본 좋아요 등록
     */
    @PostMapping("/api/post-like")
    void addPostLike(@RequestParam Long postId);

}
