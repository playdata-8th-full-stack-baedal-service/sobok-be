package com.sobok.postservice.post.service;

import com.sobok.postservice.common.dto.TokenUserInfo;
import com.sobok.postservice.common.exception.CustomException;
import com.sobok.postservice.post.client.CookFeignClient;
import com.sobok.postservice.post.client.PaymentFeignClient;
import com.sobok.postservice.post.dto.request.PostImageDto;
import com.sobok.postservice.post.dto.request.PostRegisterReqDto;
import com.sobok.postservice.post.dto.request.PostUpdateReqDto;
import com.sobok.postservice.post.dto.response.PostRegisterResDto;
import com.sobok.postservice.post.dto.response.PostUpdateResDto;
import com.sobok.postservice.post.entity.Post;
import com.sobok.postservice.post.entity.PostImage;
import com.sobok.postservice.post.repository.PostImageRepository;
import com.sobok.postservice.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PaymentFeignClient paymentClient;
    private final PostImageRepository postImageRepository;
    private final CookFeignClient cookClient;

    /**
     * 게시글 등록
     */
    public PostRegisterResDto registerPost(PostRegisterReqDto dto, TokenUserInfo userInfo) {
        Long userId = userInfo.getUserId();

        // 배달 완료 상태 확인
        boolean isCompleted = paymentClient.isPaymentCompleted(dto.getPaymentId(), userId);

        // 요리 정보 받아옴
        Long cookId = paymentClient.getCookIdByPaymentId(dto.getPaymentId());
        String cookName = cookClient.getCookNameById(cookId);

        if (!isCompleted) {
            throw new CustomException("주문이 완료되지 않아 게시글을 작성할 수 없습니다.", HttpStatus.FORBIDDEN);
        }

        Post post = Post.builder()
                .userId(userId)
                .title(dto.getTitle())
                .cookId(cookId)
                .content(dto.getContent())
                .paymentId(dto.getPaymentId())
                .build();

        postRepository.save(post);

        // 이미지 저장
        if (dto.getImages() != null) {
            for (PostImageDto imageDto : dto.getImages()) {
                PostImage postImage = PostImage.builder()
                        .postId(post.getId())
                        .imagePath(imageDto.getImageUrl())
                        .index(imageDto.getIndex())
                        .build();
                postImageRepository.save(postImage);
            }
        }

        return new PostRegisterResDto(post.getId(),cookName);
    }


    /**
     * 게시글 수정
     */
    @Transactional
    public PostUpdateResDto updatePost(PostUpdateReqDto dto, TokenUserInfo userInfo) {
        Post post = postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new CustomException("게시글이 존재하지 않습니다.", HttpStatus.NOT_FOUND));

        // 수정 권한 체크
        if (!post.getUserId().equals(userInfo.getUserId())) {
            throw new CustomException("게시글 수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        // 필드가 null이 아닐 때만 수정
        if (dto.getTitle() != null) post.setTitle(dto.getTitle());
        if (dto.getContent() != null) post.setContent(dto.getContent());

        List<PostImage> savedImages = List.of();

        // 이미지 교체 (기존 삭제 후 저장)
        if (dto.getImages() != null) {
            postImageRepository.deleteByPostId(post.getId());

            List<PostImage> newImages = dto.getImages().stream()
                    .map(img -> PostImage.builder()
                            .postId(post.getId())
                            .imagePath(img.getImageUrl())
                            .index(img.getIndex())
                            .build())
                    .toList();

            savedImages = postImageRepository.saveAll(newImages); // 저장된 이미지 리스트 유지
        }

        return PostUpdateResDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .images(
                        savedImages.stream()
                                .map(img -> new PostImageDto(img.getImagePath(), img.getIndex()))
                                .toList()
                )
                .build();
    }

}
