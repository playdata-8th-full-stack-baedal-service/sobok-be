package com.sobok.postservice.post.controller;

import com.sobok.postservice.common.dto.CommonResponse;
import com.sobok.postservice.common.dto.TokenUserInfo;
import com.sobok.postservice.post.dto.request.PostRegisterReqDto;
import com.sobok.postservice.post.dto.request.PostUpdateReqDto;
import com.sobok.postservice.post.dto.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "게시물 관리 (PostController)", description = "게시물 관련 기능 제공")
@RequestMapping("/post")
public interface PostControllerDocs {

    @Operation(
            summary = "게시물 등록",
            description = """
                    게시판에 게시물을 등록합니다. 사용자는 결제한 요리를 기반으로 후기를 남길 수 있습니다.
                    
                    ### 요청 정보
                    - 인증 필요 (Bearer Token)
                    - 요청 본문에는 게시물의 제목, 내용, 요리 ID, 결제 ID, 이미지 목록 포함
                    
                    ### 응답 정보
                    - 게시물 ID 및 요리 이름 반환
                    
                    ### 예외
                    - 유효하지 않은 요청 형식
                    - 결제 내역과 일치하지 않는 요리 ID
                    - 인증 실패
                    """
            ,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "게시물 등록 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PostRegisterResDto.class),
                            examples = @ExampleObject(
                                    name = "게시물 등록 성공 예시",
                                    value = """
                                            {
                                              "success": true,
                                              "status": 200,
                                              "message": "게시물을 등록하였습니다.",
                                              "data": {
                                                "postId": 301,
                                                "cookName": "김치찌개"
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청 형식 오류 또는 검증 실패",
                    content = @Content(
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "status": 400,
                                              "message": "요청 정보가 유효하지 않습니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "status": 401,
                                              "message": "로그인이 필요합니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/register")
    ResponseEntity<?> registerPost(
            @Parameter(hidden = true) @AuthenticationPrincipal TokenUserInfo userInfo,
            @RequestBody @Valid PostRegisterReqDto dto
    );

    @Operation(
            summary = "게시글 수정",
            description = """
                    기존에 작성한 게시글의 제목, 내용, 이미지 목록을 수정합니다.
                    
                    ### 요청 정보
                    - 인증 필요 (Bearer Token)
                    - 요청 본문에는 수정할 게시글 ID(postId), 제목, 내용, 이미지 목록 포함
                    
                    ### 응답 정보
                    - 수정된 게시글의 ID, 제목, 내용, 이미지 목록 반환
                    
                    ### 예외
                    - 존재하지 않는 게시글 ID
                    - 본인이 작성하지 않은 게시글 수정 시도
                    - 요청 데이터 유효성 실패
                    - 인증 실패
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "게시글 수정 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PostUpdateResDto.class),
                            examples = @ExampleObject(
                                    name = "게시글 수정 성공 예시",
                                    value = """
                                            {
                                              "success": true,
                                              "status": 200,
                                              "message": "게시글 수정 성공",
                                              "data": {
                                                "postId": 301,
                                                "title": "오늘의 김치찌개 후기",
                                                "content": "정말 맛있었어요! 사진도 첨부합니다.",
                                                "images": [
                                                  {
                                                    "imageUrl": "https://example.com/image1.jpg",
                                                    "index": 1
                                                  },
                                                  {
                                                    "imageUrl": "https://example.com/image2.jpg",
                                                    "index": 2
                                                  }
                                                ]
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청 형식 오류 또는 유효성 검사 실패",
                    content = @Content(
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "유효성 실패 예시",
                                    value = """
                                            {
                                              "success": false,
                                              "status": 400,
                                              "message": "제목은 공백일 수 없습니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "작성자가 아닌 사용자의 수정 시도",
                    content = @Content(
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "권한 없음 예시",
                                    value = """
                                            {
                                              "success": false,
                                              "status": 403,
                                              "message": "게시글 수정 권한이 없습니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 게시글 ID",
                    content = @Content(
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "게시글 없음 예시",
                                    value = """
                                            {
                                              "success": false,
                                              "status": 404,
                                              "message": "해당 게시글을 찾을 수 없습니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @PutMapping("/update")
    ResponseEntity<?> updatePost(
            @Parameter(hidden = true) @AuthenticationPrincipal TokenUserInfo userInfo,
            @RequestBody PostUpdateReqDto dto
    );

    @Operation(
            summary = "게시글 삭제",
            description = """
                    사용자가 자신의 게시글을 삭제합니다.
                    
                    ### 요청 정보
                    - 인증 필요 (Bearer Token)
                    - 경로 변수로 삭제할 게시글 ID를 전달합니다.
                    
                    ### 응답 정보
                    - 성공 시: 게시글 삭제 성공 메시지 반환
                    - 실패 시: 인증 실패 또는 게시글 권한 없음 등의 에러 발생
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "게시글 삭제 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "게시글 삭제 성공 예시",
                                    value = """
                                            {
                                              "success": true,
                                              "status": 200,
                                              "message": "게시글 삭제 성공",
                                              "data": "게시글 삭제 성공"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "status": 401,
                                              "message": "인증 정보가 유효하지 않습니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음 (본인의 게시글이 아님)",
                    content = @Content(
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "status": 403,
                                              "message": "게시글을 삭제할 권한이 없습니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 게시글",
                    content = @Content(
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "status": 404,
                                              "message": "해당 게시글을 찾을 수 없습니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @DeleteMapping("/delete/{postId}")
    ResponseEntity<CommonResponse<String>> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal TokenUserInfo userInfo
    );

    @Operation(
            summary = "게시물 전체 목록 조회",
            description = """
                    게시판의 전체 게시글을 페이지네이션과 정렬 조건을 기준으로 조회합니다.
                    
                    ### 요청 정보
                    - `page` : 페이지 번호 (기본값: 0)
                    - `size` : 페이지 크기 (기본값: 10)
                    - `sortBy` : 정렬 기준 (기본값: updated, 예: updated, like 등)
                    
                    ### 응답 정보
                    - 페이징 처리된 게시물 리스트와 페이지 정보 반환
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "게시물 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PagedResponse.class),
                            examples = @ExampleObject(
                                    name = "게시물 목록 조회 성공 예시",
                                    value = """
                                            {
                                              "success": true,
                                              "data": {
                                                "content": [
                                                  {
                                                    "postId": 1,
                                                    "title": "맛있는 김치찌개 후기",
                                                    "cookName": "김치찌개",
                                                    "nickName": "요리왕",
                                                    "userId": 101,
                                                    "likeCount": 25,
                                                    "thumbnail": "https://image-url.com/thumb.jpg",
                                                    "updatedAt": "2025-08-05T15:30:00"
                                                  }
                                                ],
                                                "page": 0,
                                                "size": 10,
                                                "totalElements": 1,
                                                "totalPages": 1,
                                                "last": true
                                              },
                                              "message": "전체 게시글 조회 성공",
                                              "status": 200
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 파라미터 (예: 음수 페이지 번호)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "잘못된 파라미터 예시",
                                    value = """
                                            {
                                              "success": false,
                                              "status": 400,
                                              "message": "요청 파라미터가 유효하지 않습니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/post-list")
    ResponseEntity<CommonResponse<PagedResponse<PostListResDto>>> getPostList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "updated") String sortBy
    );

    @Operation(
            summary = "특정 요리 게시글 목록 조회",
            description = """
                    특정 요리에 대해 작성된 게시글들을 정렬 조건에 따라 조회합니다.
                    
                    ### 요청 파라미터
                    - `cookId` (Path): 조회할 요리 ID
                    - `sortBy` (Query, optional): 정렬 기준 (기본값: like)
                        - 예: like, updated
                    
                    ### 응답 데이터 구조
                    - `cookId`: 요리 ID
                    - `posts`: 게시물 요약 리스트 (postId, title, thumbnail, likeCount, updatedAt 포함)
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "요리별 게시글 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CookPostGroupResDto.class),
                            examples = @ExampleObject(
                                    name = "성공 응답 예시",
                                    value = """
                                            {
                                              "success": true,
                                              "status": 200,
                                              "message": "요리별 게시글 조회 성공",
                                              "data": {
                                                "cookId": 1,
                                                "posts": [
                                                  {
                                                    "postId": 10,
                                                    "title": "김치찌개는 역시 돼지고기",
                                                    "thumbnail": "https://image-url.com/thumb1.jpg",
                                                    "likeCount": 123,
                                                    "updatedAt": "2025-08-05T13:45:00"
                                                  },
                                                  {
                                                    "postId": 11,
                                                    "title": "묵은지 김치찌개가 최고",
                                                    "thumbnail": "https://image-url.com/thumb2.jpg",
                                                    "likeCount": 98,
                                                    "updatedAt": "2025-08-04T19:00:00"
                                                  }
                                                ]
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 요리 ID에 대한 게시물이 없거나 요리 자체가 존재하지 않음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "요리 게시글 없음 예시",
                                    value = """
                                            {
                                              "success": false,
                                              "status": 404,
                                              "message": "해당 요리에 대한 게시글을 찾을 수 없습니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청 파라미터 오류 (예: 잘못된 정렬 기준)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "잘못된 정렬 기준 예시",
                                    value = """
                                            {
                                              "success": false,
                                              "status": 400,
                                              "message": "정렬 기준이 올바르지 않습니다. (like, updated만 허용)",
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/cook-posts/{cookId}")
    ResponseEntity<CookPostGroupResDto> getCookPosts(
            @PathVariable Long cookId,
            @RequestParam(defaultValue = "like") String sortBy
    );

    @Operation(
            summary = "사용자 게시글 조회",
            description = """
                    로그인한 사용자의 게시글 목록을 페이지 단위로 조회합니다.
                    
                    ### 요청 정보
                    - 인증 필요 (Bearer Token)
                    - 쿼리 파라미터
                      - `page`: 조회할 페이지 번호 (기본값 없음, 필수)
                      - `size`: 한 페이지에 표시할 게시글 수 (기본값 없음, 필수)
                    
                    ### 응답 정보
                    - `200 OK`: 페이지네이션된 게시글 목록 반환
                    
                    ### 예외
                    - `400 Bad Request`: 잘못된 쿼리 파라미터
                    - `401 Unauthorized`: 인증 실패
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "사용자 게시글 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PagedResponse.class),
                            examples = @ExampleObject(
                                    name = "사용자 게시글 조회 성공 예시",
                                    value = """
                                            {
                                              "success": true,
                                              "status": 200,
                                              "message": "성공적으로 응답이 전송되었습니다.",
                                              "data": {
                                                "content": [
                                                  {
                                                    "postId": 2,
                                                    "title": "제육볶음",
                                                    "cookName": "김치볶음밥",
                                                    "nickName": "소복이",
                                                    "userId": 1,
                                                    "likeCount": 2,
                                                    "thumbnail": "https://d3c5012dwkvoyc.cloudfront.net/post/0d8d8235-ea6b-44bd-8a04-5a4e21cb8d61javier-esteban-JEGlSCbjV9Q-unsplash.jpg",
                                                    "updatedAt": "2025-07-31T17:28:48.401808"
                                                  }
                                                ],
                                                "page": 0,
                                                "size": 10,
                                                "totalElements": 3,
                                                "totalPages": 1,
                                                "last": true
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 쿼리 파라미터",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "잘못된 쿼리 파라미터 예시",
                                    value = """
                                            {
                                              "success": false,
                                              "status": 400,
                                              "message": "page는 0 이상의 정수여야 합니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "인증 실패 예시",
                                    value = """
                                            {
                                              "success": false,
                                              "status": 401,
                                              "message": "인증 정보가 없습니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/user-post")
    ResponseEntity<?> getUserPosts(
            @Parameter(hidden = true) @AuthenticationPrincipal TokenUserInfo userInfo,
            @RequestParam int page,
            @RequestParam int size
    );

    @Operation(
            summary = "좋아요한 게시글 목록 조회",
            description = """
                    로그인한 사용자가 좋아요한 게시글 목록을 페이지 단위로 조회합니다.
                    
                    ### 요청 정보
                    - 인증 필요 (Bearer Token)
                    - 쿼리 파라미터
                      - `page`: 조회할 페이지 번호 (필수)
                      - `size`: 한 페이지에 표시할 게시글 수 (필수)
                    
                    ### 응답 정보
                    - `200 OK`: 페이지네이션된 좋아요 게시글 목록 반환
                    
                    ### 예외
                    - `400 Bad Request`: 잘못된 쿼리 파라미터
                    - `401 Unauthorized`: 인증 실패
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "좋아요한 게시글 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PagedResponse.class),
                            examples = @ExampleObject(
                                    name = "좋아요한 게시글 목록 조회 성공 예시",
                                    value = """
                                            {
                                              "success": true,
                                              "status": 200,
                                              "message": "성공적으로 응답이 전송되었습니다.",
                                              "data": {
                                                "content": [
                                                  {
                                                    "postId": 5,
                                                    "title": "맛있는 불고기 레시피",
                                                    "cookName": "불고기",
                                                    "nickName": "소복이",
                                                    "userId": 1,
                                                    "likeCount": 15,
                                                    "thumbnail": "https://example.com/image1.jpg",
                                                    "updatedAt": "2025-08-05T12:34:56"
                                                  }
                                                ],
                                                "page": 0,
                                                "size": 10,
                                                "totalElements": 5,
                                                "totalPages": 1,
                                                "last": true
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 쿼리 파라미터",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "잘못된 쿼리 파라미터 예시",
                                    value = """
                                            {
                                              "success": false,
                                              "status": 400,
                                              "message": "page는 0 이상의 정수여야 합니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "인증 실패 예시",
                                    value = """
                                            {
                                              "success": false,
                                              "status": 401,
                                              "message": "인증 정보가 없습니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/post-like")
    ResponseEntity<PagedResponse<PostListResDto>> getLikePost(
            @Parameter(hidden = true) @AuthenticationPrincipal TokenUserInfo userInfo,
            @RequestParam int page,
            @RequestParam int size
    );

    @Operation(
            summary = "게시글 상세 조회",
            description = """
                    게시글 ID로 상세 정보를 조회합니다.
                    
                    ### 요청 정보
                    - 경로 변수로 게시글 ID 전달
                    
                    ### 응답 정보
                    - 게시글 상세 정보 반환
                    
                    ### 예외
                    - `404 Not Found`: 존재하지 않는 게시글 ID
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "게시글 상세 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PostDetailResDto.class),
                            examples = @ExampleObject(
                                    name = "게시글 상세 조회 성공 예시",
                                    value = """
                                            {
                                              "success": true,
                                              "status": 200,
                                              "message": "게시글 상세 조회 성공",
                                              "data": {
                                                "postId": 10,
                                                "title": "맛있는 김치찌개 만드는 법",
                                                "cookId": 5,
                                                "cookName": "김치찌개",
                                                "nickname": "소복이",
                                                "userId": 1,
                                                "authId": 123,
                                                "likeCount": 15,
                                                "images": [
                                                  "https://example.com/image1.jpg",
                                                  "https://example.com/image2.jpg"
                                                ],
                                                "updatedAt": "2025-08-05T12:00:00",
                                                "content": "이곳에 게시글 상세 내용이 들어갑니다.",
                                                "baseIngredients": [
                                                  {
                                                    "ingredientId": 1,
                                                    "ingredientName": "김치",
                                                    "quantity": 200
                                                  }
                                                ],
                                                "additionalIngredients": [
                                                  {
                                                    "ingredientId": 3,
                                                    "ingredientName": "두부",
                                                    "quantity": 100
                                                  }
                                                ]
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "게시글을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "게시글 미존재 오류 예시",
                                    value = """
                                            {
                                              "success": false,
                                              "status": 404,
                                              "message": "해당 게시글을 찾을 수 없습니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/detail/{postId}")
    ResponseEntity<CommonResponse<PostDetailResDto>> getPostDetail(@PathVariable Long postId);

    @Operation(
            summary = "게시물 등록 여부 확인",
            description = """
                    특정 결제(paymentId)와 요리(cookId)에 대해 게시물이 등록되어 있는지 여부를 조회합니다.
                    
                    ### 요청 정보
                    - 쿼리 파라미터
                      - `paymentId` (Long, 필수): 결제 ID
                      - `cookId` (Long, 필수): 요리 ID
                    
                    ### 응답 정보
                    - 게시물 등록 여부 반환 (true / false)
                    
                    ### 예외
                    - `400 Bad Request`: 파라미터 누락 또는 형식 오류
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "게시물 등록 여부 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PostRegisterCheckResDto.class),
                            examples = @ExampleObject(
                                    name = "등록 여부 조회 성공 예시",
                                    value = """
                                            {
                                              "success": true,
                                              "status": 200,
                                              "message": "등록 여부 조회 성공",
                                              "data": {
                                                "isRegistered": true
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (파라미터 오류 등)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "잘못된 요청 예시",
                                    value = """
                                            {
                                              "success": false,
                                              "status": 400,
                                              "message": "paymentId와 cookId는 필수입니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/check-registered")
    ResponseEntity<CommonResponse<PostRegisterCheckResDto>> checkPostRegistered(
            @RequestParam Long paymentId,
            @RequestParam Long cookId);

}
