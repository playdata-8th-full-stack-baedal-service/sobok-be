package com.sobok.postservice.post.controller;

import com.sobok.postservice.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostFeignController {

    private final PostService postService;

    /**
     * 게시글 존재 여부 확인
     */
    @PostMapping("/check-post-exists")
    public Boolean checkPostExists(@RequestParam Long postId) {
        return postService.checkPostExists(postId);
    }

}
