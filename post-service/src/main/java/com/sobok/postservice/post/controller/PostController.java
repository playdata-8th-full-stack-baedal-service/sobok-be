package com.sobok.postservice.post.controller;

import com.sobok.postservice.common.dto.CommonResponse;
import com.sobok.postservice.common.dto.TokenUserInfo;
import com.sobok.postservice.post.dto.request.PostRegisterReqDto;
import com.sobok.postservice.post.dto.request.PostUpdateReqDto;
import com.sobok.postservice.post.dto.response.*;
import com.sobok.postservice.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * 게시글 등록
     */
    @PostMapping("/register")
    public ResponseEntity<PostRegisterResDto> registerPost(
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @RequestBody PostRegisterReqDto dto
    ) {
        PostRegisterResDto res = postService.registerPost(dto, userInfo);
        return ResponseEntity.ok(res);
    }

    /**
     * 게시글 수정
     */
    @PutMapping("/update")
    public ResponseEntity<CommonResponse<PostUpdateResDto>> updatePost(
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @RequestBody PostUpdateReqDto dto
    ) {
        PostUpdateResDto res = postService.updatePost(dto, userInfo);
        return ResponseEntity.ok(CommonResponse.ok(res, "게시글 수정 성공"));
    }

    /**
     * 게시글 삭제
     */
    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<CommonResponse<String>> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal TokenUserInfo userInfo
    ) {
        postService.deletePost(postId, userInfo);
        return ResponseEntity.ok(CommonResponse.ok("게시글 삭제 성공"));
    }

    /**
     * 게시글 조회
     */
    @GetMapping("/post-list")
    public ResponseEntity<CommonResponse<PagedResponse<PostListResDto>>> getPostList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "updated") String sortBy
    ) {
        PagedResponse<PostListResDto> result = postService.getPostList(page, size, sortBy);
        return ResponseEntity.ok(CommonResponse.ok(result, "전체 게시글 조회 성공"));
    }

    /**
     * 요리별 좋아요순, 최신순 정렬 조회
     */
    @GetMapping("/cook-posts/{cookId}")
    public ResponseEntity<CookPostGroupResDto> getCookPosts(
            @PathVariable Long cookId,
            @RequestParam(defaultValue = "like") String sortBy
    ) {
        return ResponseEntity.ok(postService.getCookPostsByCookId(cookId, sortBy));
    }

    /**
     * 사용자별 게시글 조회
     */
    @GetMapping("/user-post")
    public ResponseEntity<?> getUserPosts(
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @RequestParam int page,
            @RequestParam int size
    ) {
        return ResponseEntity.ok(postService.getUserPost(userInfo, page, size));
    }

    /**
     * 사용자가 좋아요한 게시글 조회
     */
    @GetMapping("/post-like")
    public ResponseEntity<PagedResponse<PostListResDto>> getLikePost(
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @RequestParam int page,
            @RequestParam int size
    ) {
        return ResponseEntity.ok(postService.getLikePost(userInfo, page, size));
    }

    /**
     * 게시글 상세 조회
     */
    @GetMapping("/detail/{postId}")
    public ResponseEntity<CommonResponse<PostDetailResDto>> getPostDetail(@PathVariable Long postId) {
        PostDetailResDto result = postService.getPostDetail(postId);
        return ResponseEntity.ok(CommonResponse.ok(result, "게시글 상세 조회 성공"));
    }

    /**
     * 게시글 존재 여부 확인 (버튼 제거용)
     */
    @GetMapping("/check-registered")
    public ResponseEntity<CommonResponse<PostRegisterCheckResDto>> checkPostRegistered(
            @RequestParam Long paymentId,
            @RequestParam Long cookId) {
        return ResponseEntity.ok(postService.getRegisterCheckStatus(paymentId, cookId));
    }


}
