package com.sobok.postservice.post.controller;

import com.sobok.postservice.common.dto.TokenUserInfo;
import com.sobok.postservice.post.dto.request.PostRegisterReqDto;
import com.sobok.postservice.post.dto.response.PostRegisterResDto;
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
            @RequestBody PostRegisterReqDto dto,
            @AuthenticationPrincipal TokenUserInfo userInfo) {
        PostRegisterResDto res = postService.registerPost(dto, userInfo);
        return ResponseEntity.ok(res);
    }

//    @GetMapping("/{postId}")
//    public ResponseEntity<PostDetailResDto> getPostDetail(@PathVariable Long postId) {
//        return ResponseEntity.ok(postService.getPostDetail(postId));
//    }
}