package com.sobok.postservice.post.service;

import com.sobok.postservice.common.dto.ApiResponse;
import com.sobok.postservice.common.dto.TokenUserInfo;
import com.sobok.postservice.common.exception.CustomException;
import com.sobok.postservice.post.client.ApiFeignClient;
import com.sobok.postservice.post.client.CookFeignClient;
import com.sobok.postservice.post.client.PaymentFeignClient;
import com.sobok.postservice.post.client.UserFeignClient;
import com.sobok.postservice.post.dto.request.*;
import com.sobok.postservice.post.dto.response.*;
import com.sobok.postservice.post.entity.Post;
import com.sobok.postservice.post.entity.PostImage;
import com.sobok.postservice.post.repository.PostImageRepository;
import com.sobok.postservice.post.repository.PostRepository;
import com.sobok.postservice.post.service.S3.S3UtilityService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final ApiFeignClient apiClient;
    private final S3UtilityService s3UtilityService;

    /**
     * 게시글 등록
     */
    @Transactional
    public PostRegisterResDto registerPost(PostRegisterReqDto dto, TokenUserInfo userInfo) {
        // 스크립트 태그 포함 여부 검사
        validateNoScriptTag(dto.getContent());

        Long userId = userInfo.getUserId();

        boolean isCompleted = paymentClient.isPaymentCompleted(dto.getPaymentId(), userId);
        if (!isCompleted) {
            throw new CustomException("주문이 완료되지 않아 게시글을 작성할 수 없습니다.", HttpStatus.FORBIDDEN);
        }

        // 게시글 중복 등록 방지
        boolean alreadyExists = postRepository.existsByPaymentIdAndCookId(dto.getPaymentId(), dto.getCookId());
        if (alreadyExists) {
            throw new CustomException("해당 요리에 대한 게시글이 이미 등록되어 있습니다.", HttpStatus.CONFLICT);
        }

        log.info(dto.getCookId().toString());
        String cookName = cookClient.getCookNameById(dto.getCookId()).getBody();

        Post post = Post.builder()
                .userId(userId)
                .title(dto.getTitle())
                .cookId(dto.getCookId())
                .content(dto.getContent())
                .paymentId(dto.getPaymentId())
                .build();
        postRepository.save(post);

        // s3에 등록
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            List<PostImage> images = buildPostImages(post.getId(), dto.getImages());
            postImageRepository.saveAll(images);
        } else {
            String url = cookClient.getCookThumbnail(dto.getCookId()).getBody();
            postImageRepository.save(
                    PostImage.builder()
                            .postId(post.getId())
                            .imagePath(url)
                            .index(1)
                            .build()
            );
        }
        PostRegisterResDto build = PostRegisterResDto.builder()
                .postId(post.getId())
                .cookName(cookName)
                .build();

        // 게시물 좋아요 등록
        userClient.addPostLike(build.getPostId());

        return build;
    }

    public void validateNoScriptTag(String html) {
        if (html != null && html.toLowerCase().contains("<script")) {
            throw new CustomException("해당 내용의 게시물은 등록할 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
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
        if (dto.getContent() != null) {
            // 스크립트 태그 포함 여부 검사
            validateNoScriptTag(dto.getContent());
            post.setContent(dto.getContent());
        }

        // 기존 이미지 S3에서 삭제
        List<PostImage> oldImages = postImageRepository.findAllByPostId(post.getId());
        deleteS3Images(oldImages);

        // 기존 이미지 삭제
        postImageRepository.deleteByPostId(post.getId());

        // 새 이미지 등록
        List<PostImage> newImages = buildPostImages(post.getId(), dto.getImages());
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

        // 이미지 S3에서 삭제
        List<PostImage> images = postImageRepository.findAllByPostId(postId);
        deleteS3Images(images); // 삭제 메서드 호출

        // DB 삭제
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
            Map<Long, String> cookNameMap = getListResponseEntity(cookIds).stream()
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
            ;
            Map<Long, String> cookNameMap = getListResponseEntity(cookIds).stream()
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

    // 리스트로 요리 이름 가져오는 공통 메서드 분리
    private List<CookNameResDto> getListResponseEntity(List<Long> cookIds) {
        ResponseEntity<List<CookNameResDto>> cookNamesByIds = cookClient.getCookNamesByIds(cookIds);
        if (cookNamesByIds.getBody() == null || cookNamesByIds.getBody().isEmpty()) {
            throw new CustomException("요리 정보를 불러오지 못했습니다.", HttpStatus.BAD_REQUEST);
        }
        return cookNamesByIds.getBody();
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

        // 좋아요 수 postIds 기준으로만 조회
        List<Long> postIds = posts.stream().map(Post::getId).toList();
        Map<Long, Long> likeCount = userClient.getLikeCountMap(postIds);

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
        Map<Long, String> cookNameMap = getListResponseEntity(cookIds).stream()
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
        // 좋아요한 게시글 Id 페이징 조회
        LikedPostPagedResDto likedPostRes = userClient.getLikedPostIds(userInfo.getUserId(), page, size);
        List<Long> postIds = likedPostRes.getContent();

        // 게시글 목록 조회
        List<Post> posts = postRepository.findAllById(postIds);

        // Id 목록 추출
        List<Long> cookIds = posts.stream().map(Post::getCookId).distinct().toList();
        List<Long> userIds = posts.stream().map(Post::getUserId).distinct().toList();

        // 외부 정보 조회
        Map<Long, Long> likeCountMap = userClient.getLikeCountMap(postIds);
        Map<Long, String> cookNameMap = getListResponseEntity(cookIds).stream()
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

        return new PagedResponse<>(
                result, page, size,
                likedPostRes.getTotalElements(),
                likedPostRes.getTotalPages(),
                likedPostRes.isLast()
        );
    }


    /**
     * 게시글 상세 조회
     */
    public PostDetailResDto getPostDetail(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException("게시글이 존재하지 않습니다.", HttpStatus.NOT_FOUND));

        String cookName = paymentClient.getCookName(post.getCookId());
        UserInfoResDto userInfo = userClient.getUserInfo(post.getUserId());
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
                .nickname(userInfo.getNickname())
                .userId(post.getUserId())
                .authId(userInfo.getAuthId())
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


    /**
     * 게시글 존재 여부 확인 (버튼 제거용)
     */
    public ApiResponse<PostRegisterCheckResDto> getRegisterCheckStatus(Long paymentId, Long cookId) {
        boolean exists = postRepository.existsByPaymentIdAndCookId(paymentId, cookId);

        String message = exists
                ? "해당 요리에 대한 게시글이 이미 등록되어 있습니다."
                : "해당 요리에 게시글을 등록할 수 있습니다.";

        return ApiResponse.ok(new PostRegisterCheckResDto(exists), message);
    }


    /**
     * S3 등록 로직 (공통)
     */
    private List<PostImage> buildPostImages(Long postId, List<PostImageDto> imageDtos) {
        if (imageDtos == null || imageDtos.isEmpty()) return Collections.emptyList(); // 이미지가 존재할 때만 수행

        return imageDtos.stream()
                .map(img -> {
                    String originalUrl = img.getImageUrl(); // 각 이미지 url 추출
                    String finalUrl = originalUrl.contains("/temp/") // url에 temp 경로 포함되어 있는지 확인
                            ? apiClient.registerImg(originalUrl) // 임시 경로일경우(/temp/) -> 영구 경로 전환
                            : originalUrl; // 임시 경로가 아닐경우 그대로 사용

                    return PostImage.builder()
                            .postId(postId)
                            .imagePath(finalUrl)
                            .index(img.getIndex())
                            .build();
                })
                .toList();
    }

    /**
     * S3 삭제 로직
     */
    private void deleteS3Images(List<PostImage> images) {
        images.forEach(img -> {
            String key = s3UtilityService.detachImageUrl(img.getImagePath());
            apiClient.deleteS3Image(key);
        });
    }

}
