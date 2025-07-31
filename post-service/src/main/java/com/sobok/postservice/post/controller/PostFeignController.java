package com.sobok.postservice.post.controller;

import com.sobok.postservice.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostFeignController {

    private final PostService postService;

    /**
     * 게시글 존재 여부 확인
     */
    @GetMapping("/check-post-exists")
    public ResponseEntity<Boolean> checkPostExists(@RequestParam Long postId) {
        return ResponseEntity.ok().body(postService.checkPostExists(postId));
    }

}
