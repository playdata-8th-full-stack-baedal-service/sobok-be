package com.sobok.postservice.post.service;

import com.sobok.postservice.common.dto.TokenUserInfo;
import com.sobok.postservice.common.exception.CustomException;
import com.sobok.postservice.post.client.CookFeignClient;
import com.sobok.postservice.post.client.PaymentFeignClient;
import com.sobok.postservice.post.dto.request.PostImageDto;
import com.sobok.postservice.post.dto.request.PostRegisterReqDto;
import com.sobok.postservice.post.dto.response.PostRegisterResDto;
import com.sobok.postservice.post.entity.Post;
import com.sobok.postservice.post.entity.PostImage;
import com.sobok.postservice.post.repository.PostImageRepository;
import com.sobok.postservice.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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

}
