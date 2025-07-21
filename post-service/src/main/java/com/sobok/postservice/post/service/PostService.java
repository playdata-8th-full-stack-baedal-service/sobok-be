package com.sobok.postservice.post.service;

import com.sobok.postservice.common.dto.ApiResponse;
import com.sobok.postservice.common.dto.TokenUserInfo;
import com.sobok.postservice.common.exception.CustomException;
import com.sobok.postservice.post.client.CookFeignClient;
import com.sobok.postservice.post.client.PaymentFeignClient;
import com.sobok.postservice.post.client.UserFeignClient;
import com.sobok.postservice.post.dto.request.*;
import com.sobok.postservice.post.dto.response.*;
import com.sobok.postservice.post.entity.Post;
import com.sobok.postservice.post.entity.PostImage;
import com.sobok.postservice.post.repository.PostImageRepository;
import com.sobok.postservice.post.repository.PostRepository;
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
    private final PostImageRepository postImageRepository;
    private final PaymentFeignClient paymentClient;
    private final CookFeignClient cookClient;
    private final UserFeignClient userClient;

    /**
     * 게시글 등록
     */
    @Transactional
    public PostRegisterResDto registerPost(PostRegisterReqDto dto, TokenUserInfo userInfo) {
        Long userId = userInfo.getUserId();

        boolean isCompleted = paymentClient.isPaymentCompleted(dto.getPaymentId(), userId);
        if (!isCompleted) {
            throw new CustomException("주문이 완료되지 않아 게시글을 작성할 수 없습니다.", HttpStatus.FORBIDDEN);
        }

        List<PostRegisterResDto.PostInfo> postInfos = new ArrayList<>();

        for (PostRegisterReqDto.PostUnitDto postDto : dto.getPosts()) {
            String cookName = cookClient.getCookNameById(postDto.getCookId());

            Post post = Post.builder()
                    .userId(userId)
                    .title(postDto.getTitle())
                    .cookId(postDto.getCookId())
                    .content(postDto.getContent())
                    .paymentId(dto.getPaymentId())
                    .build();
            postRepository.save(post);

            if (postDto.getImages() != null && !postDto.getImages().isEmpty()) {
                List<PostImage> images = postDto.getImages().stream()
                        .map(img -> PostImage.builder()
                                .postId(post.getId())
                                .imagePath(img.getImageUrl())
                                .index(img.getIndex())
                                .build())
                        .toList();
                postImageRepository.saveAll(images);
            }

            postInfos.add(PostRegisterResDto.PostInfo.builder()
                    .postId(post.getId())
                    .cookName(cookName)
                    .build());
        }

        return PostRegisterResDto.builder().posts(postInfos).build();
    }

    /**
     * 게시글 수정
     */
    @Transactional
    public PostUpdateResDto updatePost(PostUpdateReqDto dto, TokenUserInfo userInfo) {
        Post post = postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new CustomException("게시글이 존재하지 않습니다.", HttpStatus.NOT_FOUND));

        if (!post.getUserId().equals(userInfo.getUserId())) {
            throw new CustomException("해당 게시글에 대한 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        if (dto.getTitle() != null) post.setTitle(dto.getTitle());
        if (dto.getContent() != null) post.setContent(dto.getContent());

        postImageRepository.deleteByPostId(post.getId());

        List<PostImage> newImages = dto.getImages() != null ? dto.getImages().stream()
                .map(img -> PostImage.builder()
                        .postId(post.getId())
                        .imagePath(img.getImageUrl())
                        .index(img.getIndex())
                        .build())
                .toList() : Collections.emptyList();

        postImageRepository.saveAll(newImages);

        return PostUpdateResDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .images(newImages.stream()
                        .map(img -> new PostImageDto(img.getImagePath(), img.getIndex()))
                        .toList())
                .build();
    }

    /**
     * 게시글 삭제
     */
    @Transactional
    public void deletePost(Long postId, TokenUserInfo userInfo) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException("게시글이 존재하지 않습니다.", HttpStatus.NOT_FOUND));

        if (!post.getUserId().equals(userInfo.getUserId())) {
            throw new CustomException("해당 게시글에 대한 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        postImageRepository.deleteByPostId(postId);
        postRepository.delete(post);
    }

    /**
     * 게시글 조회
     */
    public PagedResponse<PostListResDto> getPostList(int page, int size, String sortBy) {
        boolean isLikeSort = "like".equalsIgnoreCase(sortBy);
        List<Post> posts;
        Map<Long, Long> likeMap;

        if (isLikeSort) {
            // 좋아요순 정렬
            LikedPostPagedResDto likedRes = userClient.getMostLikedPostIds(page, size);
            List<Long> postIds = likedRes.getContent();
            posts = postRepository.findAllById(postIds);

            Map<Long, Post> postMap = posts.stream().collect(Collectors.toMap(Post::getId, p -> p));
            posts = postIds.stream()
                    .map(postMap::get)
                    .filter(Objects::nonNull)
                    .toList();

            likeMap = userClient.getAllLikeCounts();

            // cookId 목록 가져온 후 요리 이름 조회
            List<Long> cookIds = posts.stream().map(Post::getCookId).distinct().toList();
            Map<Long, String> cookNameMap = cookClient.getCookNamesByIds(cookIds).stream()
                    .collect(Collectors.toMap(CookNameResDto::getCookId, CookNameResDto::getCookName));

            // userId 목록 가져온 후 사용자 정보 조회
            List<Long> userIds = posts.stream().map(Post::getUserId).distinct().toList();
            Map<Long, UserInfoResDto> userMap = userClient.getUserInfos(userIds);

            return new PagedResponse<>(
                    buildPostListRes(posts, likeMap, cookNameMap, userMap),
                    page, size,
                    likedRes.getTotalElements(),
                    likedRes.getTotalPages(),
                    likedRes.isLast()
            );

        } else {
            // 최신순 정렬
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
            Page<Post> postPage = postRepository.findAll(pageable);
            posts = postPage.getContent();
            likeMap = userClient.getAllLikeCounts();

            // cookId 목록 가져온 후 요리 이름 조회
            List<Long> cookIds = posts.stream().map(Post::getCookId).distinct().toList();
            Map<Long, String> cookNameMap = cookClient.getCookNamesByIds(cookIds).stream()
                    .collect(Collectors.toMap(CookNameResDto::getCookId, CookNameResDto::getCookName));

            // userId 목록 가져온 후 사용자 정보 조회
            List<Long> userIds = posts.stream().map(Post::getUserId).distinct().toList();
            Map<Long, UserInfoResDto> userMap = userClient.getUserInfos(userIds);

            return new PagedResponse<>(
                    buildPostListRes(posts, likeMap, cookNameMap, userMap),
                    page, size,
                    postPage.getTotalElements(),
                    postPage.getTotalPages(),
                    postPage.isLast()
            );

        }

    }

    // 공통 메서드 분리
    private List<PostListResDto> buildPostListRes(
            List<Post> posts,
            Map<Long, Long> likeMap,
            Map<Long, String> cookNameMap,
            Map<Long, UserInfoResDto> userMap
    ) {
        return posts.stream().map(post -> {
            String cookName = cookNameMap.get(post.getCookId());
            UserInfoResDto user = userMap.get(post.getUserId());
            String thumbnail = postImageRepository.findTopByPostIdOrderByIndexAsc(post.getId())
                    .map(PostImage::getImagePath).orElse(null);

            return PostListResDto.builder()
                    .postId(post.getId())
                    .title(post.getTitle())
                    .cookName(cookName)
                    .userId(user.getUserId())
                    .nickName(user.getNickname())
                    .likeCount(likeMap.getOrDefault(post.getId(), 0L))
                    .thumbnail(thumbnail)
                    .updatedAt(post.getUpdatedAt())
                    .build();
        }).toList();
    }

    /**
     * 요리별 요리별 좋아요순, 최신순 정렬 조회
     */
    public CookPostGroupResDto getCookPostsByCookId(Long cookId, String sortBy) {
        List<Post> posts = postRepository.findByCookId(cookId);

        if (posts.isEmpty()) {
            throw new CustomException("해당 요리에 대한 게시글이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
        }

        Map<Long, Long> likeCount = userClient.getAllLikeCounts();

        List<CookPostGroupResDto.PostSummaryDto> summaries = posts.stream().map(post -> {
            Long postId = post.getId();
            String title = post.getTitle();
            String thumbnail = postImageRepository.findTopByPostIdOrderByIndexAsc(postId)
                    .map(PostImage::getImagePath).orElse(null);
            Long updatedAt = post.getUpdatedAt();

            return CookPostGroupResDto.PostSummaryDto.builder()
                    .postId(postId)
                    .title(title)
                    .thumbnail(thumbnail)
                    .likeCount(likeCount.getOrDefault(postId, 0L))
                    .updatedAt(updatedAt)
                    .build();
        }).collect(Collectors.toList());

        // 정렬
        if ("like".equalsIgnoreCase(sortBy)) {
            summaries.sort((a, b) -> Long.compare(
                    b.getLikeCount() != null ? b.getLikeCount() : 0,
                    a.getLikeCount() != null ? a.getLikeCount() : 0
            ));
        } else {
            summaries.sort((a, b) -> {
                if (a.getUpdatedAt() == null) return 1;
                if (b.getUpdatedAt() == null) return -1;
                return b.getUpdatedAt().compareTo(a.getUpdatedAt());
            });
        }
        return CookPostGroupResDto.builder()
                .cookId(cookId)
                .posts(summaries)
                .build();
    }

    /**
     * 사용자별 게시글 조회
     */
    public ApiResponse<PagedResponse<PostListResDto>> getUserPost(TokenUserInfo userInfo, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<Post> postPage = postRepository.findAllByUserId(userInfo.getUserId(), pageable);
        List<Post> posts = postPage.getContent();

        //Id 목록 추출
        List<Long> postIds = posts.stream().map(Post::getId).toList();
        List<Long> cookIds = posts.stream().map(Post::getCookId).distinct().toList();
        List<Long> userIds = posts.stream().map(Post::getUserId).distinct().toList();

        Map<Long, Long> likeCountMap = userClient.getLikeCountMap(postIds);
        Map<Long, String> cookNameMap = cookClient.getCookNamesByIds(cookIds).stream()
                .collect(Collectors.toMap(CookNameResDto::getCookId, CookNameResDto::getCookName));
        Map<Long, UserInfoResDto> userInfoMap = userClient.getUserInfos(userIds);

        List<PostListResDto> result = posts.stream().map(post -> {
            Long postId = post.getId();
            Long cookId = post.getCookId();
            Long userId = post.getUserId();

            return PostListResDto.builder()
                    .postId(postId)
                    .title(post.getTitle())
                    .cookName(cookNameMap.get(cookId))
                    .userId(userId)
                    .nickName(userInfoMap.get(userId).getNickname())
                    .likeCount(likeCountMap.getOrDefault(postId, 0L))
                    .thumbnail(postImageRepository.findTopByPostIdOrderByIndexAsc(postId)
                            .map(PostImage::getImagePath).orElse(null))
                    .updatedAt(post.getUpdatedAt())
                    .build();
        }).toList();

        return ApiResponse.ok(new PagedResponse<>(result, page, size,
                postPage.getTotalElements(), postPage.getTotalPages(), postPage.isLast()));
    }

    /**
     * 사용자가 좋아요한 게시글 조회
     */
    public PagedResponse<PostListResDto> getLikePost(TokenUserInfo userInfo, int page, int size) {
        LikedPostPagedResDto likedPostRes = userClient.getLikedPostIds(userInfo.getUserId(), page, size);

        List<Post> posts = postRepository.findAllById(likedPostRes.getContent());

        List<PostListResDto> result = posts.stream().map(post -> {
            String cookName = cookClient.getCookNameById(post.getCookId());
            UserInfoResDto user = userClient.getUserInfo(post.getUserId());
            Long likeCount = userClient.getLikeCount(post.getId());
            String thumbnail = postImageRepository.findTopByPostIdOrderByIndexAsc(post.getId())
                    .map(PostImage::getImagePath).orElse(null);

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

        return new PagedResponse<>(result, page, size,
                likedPostRes.getTotalElements(), likedPostRes.getTotalPages(), likedPostRes.isLast());
    }

    /**
     * 게시글 상세 조회
     */
    public PostDetailResDto getPostDetail(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException("게시글이 존재하지 않습니다.", HttpStatus.NOT_FOUND));

        String cookName = paymentClient.getCookName(post.getCookId());
        String nickname = userClient.getNicknameById(post.getUserId());
        Long likeCount = userClient.getLikeCount(postId);

        List<String> imagePaths = postImageRepository.findAllByPostId(postId).stream()
                .sorted(Comparator.comparingInt(PostImage::getIndex))
                .map(PostImage::getImagePath)
                .toList();

        Long cartCookId = paymentClient.getCartCookIdByPaymentId(post.getPaymentId());
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

    /**
     * 게시글 존재 여부 확인
     */
    public Boolean checkPostExists(Long postId) {
        return postRepository.existsById(postId);
    }
}
