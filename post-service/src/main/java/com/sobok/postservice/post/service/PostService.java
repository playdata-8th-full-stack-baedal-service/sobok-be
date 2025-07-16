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
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


import java.util.*;
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
        Page<Post> postPage;

        if ("like".equalsIgnoreCase(sortBy)) {
            List<Long> postIds = queryFactory
                    .select(post.id)
                    .from(post)
                    .leftJoin(userLike).on(post.id.eq(userLike.postId))
                    .groupBy(post.id, post.updatedAt)
                    .orderBy(userLike.count().desc(), post.updatedAt.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

            if (postIds.isEmpty()) {
                return new PagedResponse<>(Collections.emptyList(), page, size, 0, 0, true);
            }

            List<Post> content = postRepository.findAllById(postIds).stream()
                    .sorted(Comparator.comparing(p -> postIds.indexOf(p.getId())))
                    .toList();

            Long total = queryFactory.select(post.id.countDistinct()).from(post).fetchOne();
            postPage = new PageImpl<>(content, pageable, total != null ? total : 0);
        } else {
            postPage = postRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt")));
        }

        if (postPage.isEmpty()) {
            throw new CustomException("게시글이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
        }

        List<PostListResDto> result = postListResDtos(postPage.getContent());

        return new PagedResponse<>(
                result,
                page,
                size,
                postPage.getTotalElements(),
                postPage.getTotalPages(),
                postPage.isLast()
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

        List<PostListResDto> posts = postListResDtos(postPage.getContent());

        PagedResponse<PostListResDto> response = new PagedResponse<>(
                posts,
                page,
                size,
                postPage.getTotalElements(),
                postPage.getTotalPages(),
                postPage.isLast()
        );

        String message = posts.isEmpty()
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

        List<Post> posts = postRepository.findAllById(
                userLikePage.getContent().stream().map(UserLike::getPostId).toList()
        );

        List<PostListResDto> result = postListResDtos(posts);

        return new PagedResponse<>(
                result,
                page,
                size,
                userLikePage.getTotalElements(),
                userLikePage.getTotalPages(),
                userLikePage.isLast()
        );
    }

    /**
     * 게시글 목록 조회 시 필요한 정보(공통된 응답이라 분리) N+1 문제 해결
     */
    private List<PostListResDto> postListResDtos(List<Post> posts) {
        if (posts.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> postIds = posts.stream().map(Post::getId).toList();

        // N+1 문제 해결 - QueryDSL을 사용하여 좋아요 수, 썸네일을 한번의 쿼리로 가져옴
        Map<Long, Long> likeCountMap = queryFactory
                .select(userLike.postId, userLike.count())
                .from(userLike)
                .where(userLike.postId.in(postIds))
                .groupBy(userLike.postId)
                .fetch().stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(userLike.postId),
                        tuple -> tuple.get(userLike.count())
                ));

        Map<Long, String> thumbnailMap = postImageRepository.findAllByPostIdInAndIndex(postIds, 1)
                .stream()
                .collect(Collectors.toMap(PostImage::getPostId, PostImage::getImagePath, (first, second) -> first));

        return posts.stream().map(post -> {
            String cookName = cookClient.getCookNameById(post.getCookId());
            UserInfoResDto user = userClient.getUserInfo(post.getUserId());

            return PostListResDto.builder()
                    .postId(post.getId())
                    .title(post.getTitle())
                    .cookName(cookName)
                    .userId(user.getUserId())
                    .nickName(user.getNickname())
                    .likeCount(likeCountMap.getOrDefault(post.getId(), 0L).intValue())
                    .thumbnail(thumbnailMap.get(post.getId()))
                    .updatedAt(post.getUpdatedAt())
                    .build();
        }).toList();
    }

    /**
     * 게시글 좋아요 등록
     */
    public UserLikeResDto likePost(TokenUserInfo userInfo, Long postId) {
        Long userId = userInfo.getUserId();

        if (!postRepository.existsById(postId)) {
            throw new CustomException("게시글이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
        }

        if (userLikeRepository.findByUserIdAndPostId(userId, postId).isPresent()) {
            throw new CustomException("이미 좋아요한 게시글입니다.", HttpStatus.BAD_REQUEST);
        }

        userLikeRepository.save(
                UserLike.builder()
                        .userId(userId)
                        .postId(postId)
                        .build()
        );
        return UserLikeResDto.builder()
                .postId(postId)
                .build();
    }

    /**
     * 게시글 좋아요 해제
     */
    public UserLikeResDto unlikePost(TokenUserInfo userInfo, Long postId) {
        Long userId = userInfo.getUserId();

        if (!postRepository.existsById(postId)) {
            throw new CustomException("게시글이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
        }

        UserLike like = userLikeRepository.findByUserIdAndPostId(userId, postId)
                .orElseThrow(() -> new CustomException("좋아요 정보가 없습니다.", HttpStatus.NOT_FOUND));

        userLikeRepository.delete(like);

        return UserLikeResDto.builder()
                .postId(postId)
                .build();
    }

    /**
     * 게시글 상세 조회
     */
    public PostDetailResDto getPostDetail(Long postId) {
        // 게시글 조회
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new CustomException("게시글을 찾을 수 없습니다. id=" + postId, HttpStatus.NOT_FOUND));

        // 요리 이름, 작성자 닉네임, 좋아요 수 조회
        String cookName = paymentClient.getCookName(post.getCookId());
        String nickname = userClient.getNicknameById(post.getUserId());
        int likeCount = userLikeRepository.countByPostId(postId);

        // 이미지 목록 조회 및 정렬
        List<String> imagePaths = postImageRepository.findAllByPostId(postId).stream()
                .sorted(Comparator.comparingInt(PostImage::getIndex))
                .map(PostImage::getImagePath)
                .toList();

        // 결제 - cartCookId 조회
        Long cartCookId = paymentClient.getCartCookIdByPaymentId(post.getPaymentId());

        // 기본 식재료 및 추가 식재료 조회
        List<IngredientResDto> defaultIngredients = paymentClient.getDefaultIngredients(post.getCookId());
        List<IngredientResDto> extraIngredients = paymentClient.getExtraIngredients(cartCookId);

        return PostDetailResDto.builder()
                .postId(postId)
                .title(post.getTitle())
                .cookName(cookName)
                .nickname(nickname)
                .userId(post.getUserId())
                .likeCount(likeCount)
                .images(imagePaths)
                .updatedAt(post.getUpdatedAt())
                .content(post.getContent())
                .defaultIngredients(defaultIngredients)
                .extraIngredients(extraIngredients)
                .build();
    }

}
