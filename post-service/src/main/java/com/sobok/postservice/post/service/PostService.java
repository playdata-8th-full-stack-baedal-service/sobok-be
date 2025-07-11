package com.sobok.postservice.post.service;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import static com.sobok.postservice.post.entity.QPost.post;
import static com.sobok.postservice.post.entity.QPostImage.postImage;
import static com.sobok.postservice.post.entity.QUserLike.userLike;

import com.sobok.postservice.common.dto.ApiResponse;
import com.sobok.postservice.common.dto.TokenUserInfo;
import com.sobok.postservice.common.exception.CustomException;
import com.sobok.postservice.post.client.CookFeignClient;
import com.sobok.postservice.post.client.PaymentFeignClient;
import com.sobok.postservice.post.client.UserFeignClient;
import com.sobok.postservice.post.dto.request.PostImageDto;
import com.sobok.postservice.post.dto.request.PostRegisterReqDto;
import com.sobok.postservice.post.dto.request.PostUpdateReqDto;
import com.sobok.postservice.post.dto.response.*;
import com.sobok.postservice.post.entity.*;
import com.sobok.postservice.post.repository.PostImageRepository;
import com.sobok.postservice.post.repository.PostRepository;
import com.sobok.postservice.post.repository.UserLikeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PaymentFeignClient paymentClient;
    private final PostImageRepository postImageRepository;
    private final CookFeignClient cookClient;
    private final UserFeignClient userClient;
    private final UserLikeRepository userLikeRepository;
    private final JPAQueryFactory queryFactory;

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

        return new PostRegisterResDto(post.getId(), cookName);
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
    /**
     * todo: s3 연결 필요
     */


    /**
     * 게시글 수정
     */
    @Transactional
    public void deletePost(Long postId, TokenUserInfo userInfo) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException("게시글이 존재하지 않습니다.", HttpStatus.NOT_FOUND));

        if (!post.getUserId().equals(userInfo.getUserId())) {
            throw new CustomException("게시글 삭제 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        // 관련 이미지 먼저 삭제
        postImageRepository.deleteByPostId(post.getId());

        // 게시글 삭제
        postRepository.delete(post);
    }
    /**
     * todo: s3 연결 필요
     */

    /**
     * 게시글 조회
     */
    public PagedResponse<PostListResDto> getPostList(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size);

        List<Post> content;
        long total;

        if ("like".equalsIgnoreCase(sortBy)) {
            // 좋아요 순 정렬
            content = queryFactory
                    .selectFrom(post)
                    .leftJoin(userLike).on(post.id.eq(userLike.postId))
                    .groupBy(post.id)
                    .orderBy(userLike.count().desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

            total = queryFactory.select(post.count()).from(post).fetchOne();

        } else {
            // 최신순 정렬
            Page<Post> postPage = postRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt")));
            content = postPage.getContent();
            total = postPage.getTotalElements();
        }

        if (content.isEmpty()) {
            throw new CustomException("게시글이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
        }

        List<PostListResDto> result = content.stream().map(post -> {
            String cookName = cookClient.getCookNameById(post.getCookId());
            UserInfoResDto user = userClient.getUserInfo(post.getUserId());

            String thumbnail = postImageRepository.findByPostIdAndIndex(post.getId(), 1)
                    .map(PostImage::getImagePath)
                    .orElse(null);

            int likeCount = userLikeRepository.countByPostId(post.getId());

            return PostListResDto.builder()
                    .postId(post.getId())
                    .title(post.getTitle())
                    .cookName(cookName)
                    .userId(user.getUserId())
                    .nickName(user.getNickname())
                    .likeCount(likeCount)
                    .thumbnail(thumbnail)
                    .updatedAt(post.getUpdatedAt())
                    .build();
        }).toList();

        return new PagedResponse<>(
                result,
                page,
                size,
                total,
                (int) Math.ceil((double) total / size),
                result.size() < size
        );
    }

    /**
     * 요리별 좋아요순, 최신순 게시글 조회
     */
    public CookPostGroupResDto getCookPostsByCookId(Long cookId, String sortBy) {
        QPost post = QPost.post;
        QUserLike userLike = QUserLike.userLike;
        QPostImage postImage = QPostImage.postImage;

        NumberExpression<Long> likeCount = userLike.countDistinct();

        List<CookPostGroupResDto.PostSummaryDto> posts = queryFactory
                .select(Projections.constructor(CookPostGroupResDto.PostSummaryDto.class,
                        post.id,
                        post.title,
                        JPAExpressions
                                .select(postImage.imagePath)
                                .from(postImage)
                                .where(postImage.postId.eq(post.id), postImage.index.eq(1))
                                .limit(1),
                        likeCount
                ))
                .from(post)
                .leftJoin(userLike).on(userLike.postId.eq(post.id))
                .where(post.cookId.eq(cookId))
                .groupBy(post.id, post.title, post.updatedAt)
                .orderBy(
                        "like".equalsIgnoreCase(sortBy)
                                ? likeCount.desc()
                                : post.updatedAt.desc()
                )
                .fetch();


        if (posts.isEmpty()) {
            throw new CustomException("해당 요리에 대한 게시글이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
        }

        return CookPostGroupResDto.builder()
                .cookId(cookId)
                .posts(posts)
                .build();
    }

    // todo s3 연결 필요 썸네일

    /**
     * 사용자별 게시글 조회
     */
    public ApiResponse<PagedResponse<PostListResDto>> getUserPost(TokenUserInfo userInfo, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<Post> postPage = postRepository.findAllByUserId(userInfo.getUserId(), pageable);

        List<PostListResDto> posts = postPage.getContent().stream().map(post -> {
            return getPostListResDto(post);
        }).toList();

        PagedResponse<PostListResDto> response = new PagedResponse<>(
                posts,
                page,
                size,
                postPage.getTotalElements(),
                postPage.getTotalPages(),
                postPage.isLast()
        );

        String message = posts.isEmpty() // 게시글 없어도 빈 배열로 보내기 위해 사용
                ? "작성한 게시글이 없습니다."
                : "사용자 게시글 조회 성공";

        return ApiResponse.ok(response, message);
    }
    // todo s3 연결 필요 썸네일


    /**
     * 사용자가 좋아요한 게시글 조회
     */
    public PagedResponse<PostListResDto> getLikePost(TokenUserInfo userInfo, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Long userId = userInfo.getUserId();

        Page<UserLike> userLikePage = userLikeRepository.findAllByUserId(userId, pageable);

        List<PostListResDto> result = userLikePage.getContent().stream().map(userLike -> {
            Post post = postRepository.findById(userLike.getPostId())
                    .orElseThrow(() -> new CustomException("게시글이 존재하지 않습니다.", HttpStatus.NOT_FOUND));

            return getPostListResDto(post);
        }).toList();

        return new PagedResponse<>(
                result,
                page,
                size,
                userLikePage.getTotalElements(),
                userLikePage.getTotalPages(),
                userLikePage.isLast()
        );
    }

    //게시글 목록 조회 시 필요한 정보(공통된 응답이라 분리)
    private PostListResDto getPostListResDto(Post post) {
        String cookName = cookClient.getCookNameById(post.getCookId());
        String nickName = userClient.getNicknameById(post.getUserId());
        int likeCount = userLikeRepository.countByPostId(post.getId());
        String thumbnail = postImageRepository.findTopByPostIdOrderByIndexAsc(post.getId())
                .map(PostImage::getImagePath).orElse(null);

        return PostListResDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .cookName(cookName)
                .nickName(nickName)
                .userId(post.getUserId())
                .likeCount(likeCount)
                .thumbnail(thumbnail)
                .updatedAt(post.getUpdatedAt())
                .build();
    }


}
