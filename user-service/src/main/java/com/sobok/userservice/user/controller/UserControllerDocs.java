package com.sobok.userservice.user.controller;

import com.sobok.userservice.common.dto.CommonResponse;
import com.sobok.userservice.common.dto.TokenUserInfo;
import com.sobok.userservice.user.dto.email.UserEmailDto;
import com.sobok.userservice.user.dto.info.UserAddressDto;
import com.sobok.userservice.user.dto.request.*;
import com.sobok.userservice.user.dto.response.PreOrderUserResDto;
import com.sobok.userservice.user.dto.response.UserLikeResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "사용자 관리 (UserController)", description = "주소, 이메일, 즐겨찾기, 사진 등 사용자 관련 기능 제공")
@RequestMapping("/user")
public interface UserControllerDocs {

    @Operation(
            summary = "주소 추가",
            description = """
                    사용자의 주소를 추가합니다.
                    
                    ### 요청 형식
                    - 로그인한 사용자의 토큰 필요
                    - 요청 본문에 도로명 주소와 상세 주소를 포함
                    
                    ### 응답 형식
                    - `200 OK`: 주소 추가 성공
                    - `400 Bad Request`: 잘못된 요청 (빈 필드, 형식 오류 등)
                    - `401 Unauthorized`: 인증 실패
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "주소 저장 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "주소 저장 성공 예시",
                                    value = """
                                            {
                                              "status": 200,
                                              "message": "성공적으로 주소가 저장되었습니다.",
                                              "data": 1
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 필드 누락, 형식 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "잘못된 요청 예시",
                                    value = """
                                            {
                                              "status": 400,
                                              "message": "도로명 주소는 필수입니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "JWT 토큰 인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "인증 실패 예시",
                                    value = """
                                            {
                                              "status": 401,
                                              "message": "인증되지 않은 사용자입니다."
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/addAddress")
    ResponseEntity<?> addAddress(
            @Parameter(hidden = true) @AuthenticationPrincipal TokenUserInfo userInfo,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "등록할 주소 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserAddressReqDto.class))
            )
            @RequestBody UserAddressReqDto userAddressReqDto
    );


    @Operation(
            summary = "주소 수정",
            description = "사용자의 주소를 수정합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "주소 수정 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "status": "200",
                                                "message": "성공적으로 주소가 변경되었습니다.",
                                                "data": 5
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (주소 미입력, ID 없음 등)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "status": "400",
                                                "message": "주소는 필수 입니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 주소 ID",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "status": "404",
                                                "message": "해당 주소를 찾을 수 없습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    @PatchMapping("/editAddress")
    ResponseEntity<?> editAddress(@Parameter(hidden = true) @AuthenticationPrincipal TokenUserInfo userInfo,
                                  @RequestBody
                                  @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                          description = "수정할 주소 정보",
                                          required = true,
                                          content = @Content(schema = @Schema(implementation = UserAddressEditReqDto.class))
                                  ) UserAddressEditReqDto reqDto);

    @Operation(
            summary = "주소 조회",
            description = "사용자의 주소 목록을 조회합니다.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "주소 목록 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserAddressDto.class),
                                    examples = @ExampleObject(
                                            value = """
                                                        {
                                                            "success": true,
                                                            "data": [
                                                                {
                                                                    "id": 1,
                                                                    "roadFull": "서울시 강남구 테헤란로",
                                                                    "addrDetail": "13층 1301호"
                                                                },
                                                                {
                                                                    "id": 2,
                                                                    "roadFull": "서울 종로구 비봉길 1 (구기동)",
                                                                    "addrDetail": "10층 1002호"
                                                                }
                                                            ],
                                                            "message": "사용자의 주소를 성공적으로 조회하였습니다.",
                                                            "status": 200
                                                        }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증되지 않은 사용자",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                                        {
                                                            "status": "401",
                                                            "message": "인증되지 않은 사용자입니다."
                                                        }
                                                    """
                                    )
                            )
                    )
            }
    )
    @GetMapping("/getAddress")
    ResponseEntity<?> getAddress(@Parameter(hidden = true) @AuthenticationPrincipal TokenUserInfo userInfo);

    @Operation(
            summary = "주소 삭제",
            description = "사용자의 주소를 삭제합니다.",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "삭제할 주소 ID",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "1"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "주소 삭제 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "success": true,
                                                        "data": 1,
                                                        "message": "사용자의 주소를 성공적으로 삭제하였습니다.",
                                                        "status": 200
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "존재하지 않는 주소 ID",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "success": false,
                                                        "message": "존재하지 않는 주소입니다.",
                                                        "status": 404
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "권한이 없는 사용자",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "success": false,
                                                        "message": "해당 주소에 대한 삭제 권한이 없습니다.",
                                                        "status": 403
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    @DeleteMapping("/deleteAddress/{id}")
    ResponseEntity<?> deleteAddress(@Parameter(hidden = true) @AuthenticationPrincipal TokenUserInfo userInfo,
                                    @PathVariable Long id);

    @Operation(
            summary = "이메일 수정",
            description = "사용자의 이메일을 수정합니다. 유효한 이메일 형식이어야 합니다.",
            security = @SecurityRequirement(name = "bearerAuth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "수정할 이메일 정보",
                    content = @Content(schema = @Schema(implementation = UserEmailDto.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "이메일 수정 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(value = """
                                                {
                                                  "success": true,
                                                  "data": 1,
                                                  "message": "사용자의 이메일을 성공적으로 변경하였습니다.",
                                                  "status": 200
                                                }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 (이메일 형식 오류 등)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(value = """
                                                {
                                                  "success": false,
                                                  "message": "유효한 이메일 형식이어야 합니다.",
                                                  "status": 400
                                                }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증 실패 (토큰 누락 또는 유효하지 않음)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(value = """
                                                {
                                                  "success": false,
                                                  "message": "인증 정보가 유효하지 않습니다.",
                                                  "status": 401
                                                }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "이메일 중복",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(value = """
                                                {
                                                  "success": false,
                                                  "message": "이미 존재하는 이메일입니다.",
                                                  "status": 409
                                                }
                                            """)
                            )
                    )
            }
    )
    @PostMapping("/editEmail")
    ResponseEntity<?> editEmail(@Parameter(hidden = true) @AuthenticationPrincipal TokenUserInfo userInfo,
                                @Valid @RequestBody UserEmailDto reqDto);

    @Operation(
            summary = "이메일 삭제",
            description = "사용자의 이메일을 삭제합니다.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "이메일 삭제 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "success": true,
                                                        "data": 1,
                                                        "message": "사용자의 이메일을 성공적으로 삭제하였습니다.",
                                                        "status": 200
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "삭제할 이메일이 존재하지 않음",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "success": false,
                                                        "message": "삭제할 이메일이 존재하지 않습니다.",
                                                        "status": 404
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
                                            value = """
                                                    {
                                                        "success": false,
                                                        "message": "인증되지 않은 사용자입니다.",
                                                        "status": 401
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    @DeleteMapping("/deleteEmail")
    ResponseEntity<?> deleteEmail(@Parameter(hidden = true) @AuthenticationPrincipal TokenUserInfo userInfo);

    @Operation(
            summary = "전화번호 수정",
            description = "사용자의 전화번호를 수정합니다. 전화번호와 인증코드(userInputCode)를 함께 전달해야 합니다.",
            security = @SecurityRequirement(name = "bearerAuth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "전화번호 및 인증 코드 정보",
                    content = @Content(schema = @Schema(implementation = UserPhoneDto.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "전화번호 수정 성공",
                            content = @Content(schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "success": true,
                                              "data": 1,
                                              "message": "사용자의 전화번호를 성공적으로 변경하였습니다.",
                                              "status": 200
                                            }
                                            """))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 (전화번호 형식 오류 또는 인증코드 오류)",
                            content = @Content(schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "success": false,
                                              "message": "전화번호는 - 없이 숫자만 11자리여야 합니다.",
                                              "status": 400
                                            }
                                            """))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증 실패 (유효하지 않은 토큰 등)",
                            content = @Content(schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "success": false,
                                              "message": "인증되지 않은 사용자입니다.",
                                              "status": 401
                                            }
                                            """))
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "중복된 전화번호",
                            content = @Content(schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "success": false,
                                              "message": "이미 사용 중인 전화번호입니다.",
                                              "status": 409
                                            }
                                            """))
                    )
            }
    )
    @PatchMapping("/editPhone")
    ResponseEntity<?> editPhone(@Parameter(hidden = true) @AuthenticationPrincipal TokenUserInfo userInfo,
                                @RequestBody @Valid UserPhoneDto userPhoneDto);

    @Operation(
            summary = "즐겨찾기 등록",
            description = "사용자가 특정 요리를 즐겨찾기에 등록합니다.",
            security = @SecurityRequirement(name = "bearerAuth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = UserBookmarkReqDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "즐겨찾기 등록 예시",
                                            value = "{\n  \"cookId\": 12\n}"
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "즐겨찾기 등록 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{\n  \"success\": true,\n  \"data\": {\n    \"cookId\": 12\n  },\n  \"message\": \"해당 요리가 즐겨찾기에 등록되었습니다.\",\n  \"status\": 200\n}")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "cookId 누락 또는 잘못된 요청",
                            content = @Content(
                                    schema = @Schema(example = "{\n  \"success\": false,\n  \"message\": \"cookId는 필수입니다.\",\n  \"status\": 400\n}")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "해당 요리를 찾을 수 없음",
                            content = @Content(
                                    schema = @Schema(example = "{\n  \"success\": false,\n  \"message\": \"요리를 찾을 수 없습니다.\",\n  \"status\": 404\n}")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "이미 즐겨찾기에 등록된 경우",
                            content = @Content(
                                    schema = @Schema(example = "{\n  \"success\": false,\n  \"message\": \"이미 즐겨찾기에 등록된 요리입니다.\",\n  \"status\": 409\n}")
                            )
                    )
            }
    )
    @PostMapping("/addBookmark")
    ResponseEntity<?> addBookmark(@Parameter(hidden = true) @AuthenticationPrincipal TokenUserInfo userInfo,
                                  @RequestBody UserBookmarkReqDto userBookmarkReqDto);

    @Operation(
            summary = "즐겨찾기 삭제",
            description = "즐겨찾기에 등록된 요리를 삭제합니다.",
            security = @SecurityRequirement(name = "bearerAuth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "즐겨찾기 삭제 요청 (cookId 필수)",
                    content = @Content(schema = @Schema(implementation = UserBookmarkReqDto.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "즐겨찾기 삭제 성공",
                            content = @Content(schema = @Schema(implementation = UserBookmarkReqDto.class),
                                    examples = @ExampleObject(value = """
                                                {
                                                  "success": true,
                                                  "data": {
                                                    "cookId": 123
                                                  },
                                                  "message": "해당 요리가 즐겨찾기 해제되었습니다.",
                                                  "status": 200
                                                }
                                            """))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증 실패",
                            content = @Content(schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(value = """
                                                {
                                                  "success": false,
                                                  "message": "인증되지 않은 사용자입니다.",
                                                  "status": 401
                                                }
                                            """))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "즐겨찾기 항목을 찾을 수 없음",
                            content = @Content(schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(value = """
                                                {
                                                  "success": false,
                                                  "message": "해당 즐겨찾기 항목이 존재하지 않습니다.",
                                                  "status": 404
                                                }
                                            """))
                    )
            }
    )
    @PostMapping("/deleteBookmark")
    ResponseEntity<?> deleteBookmark(@Parameter(hidden = true)@AuthenticationPrincipal TokenUserInfo userInfo,
                                     @RequestBody UserBookmarkReqDto userBookmarkReqDto);

    @Operation(
            summary = "즐겨찾기 요리 조회",
            description = "로그인한 사용자의 즐겨찾기 요리 목록을 조회합니다.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "즐겨찾기 요리 조회 성공",
                            content = @Content(
                                    schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "success": true,
                                              "data": [
                                                {
                                                  "cookId": 1,
                                                  "cookName": "김치찌개",
                                                  "thumbnail": "kimchi_stew.png"
                                                },
                                                {
                                                  "cookId": 2,
                                                  "cookName": "된장찌개",
                                                  "thumbnail": "soybean_stew.png"
                                                }
                                              ],
                                              "message": "즐겨찾기 요리가 조회되었습니다.",
                                              "status": 200
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "204",
                            description = "즐겨찾기 요리 없음",
                            content = @Content(
                                    schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "success": true,
                                              "data": null,
                                              "message": "콘텐츠가 없습니다.",
                                              "status": 204
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증 실패 (유효하지 않은 토큰 등)",
                            content = @Content(
                                    schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "success": false,
                                              "message": "인증되지 않은 사용자입니다.",
                                              "status": 401
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 내부 오류",
                            content = @Content(
                                    schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "success": false,
                                              "message": "서버 오류가 발생했습니다.",
                                              "status": 500
                                            }
                                            """)
                            )
                    )
            }
    )
    @GetMapping("/getBookmark")
    ResponseEntity<?> getBookmark(@Parameter(hidden = true) @AuthenticationPrincipal TokenUserInfo userInfo);

    @Operation(
            summary = "특정 요리에 대한 즐겨찾기 상태 조회",
            description = "사용자의 특정 요리에 대한 즐겨찾기 여부를 반환합니다.",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "조회할 요리의 ID",
                            required = true,
                            example = "5"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "즐겨찾기 상태 조회 성공",
                            content = @Content(schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "success": true,
                                              "data": true,
                                              "message": "사용자의 즐겨찾기 상태가 조회되었습니다.",
                                              "status": 200
                                            }
                                            """))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요리 ID 형식",
                            content = @Content(schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "success": false,
                                              "message": "잘못된 요청입니다. 요리 ID는 양수여야 합니다.",
                                              "status": 400
                                            }
                                            """))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증 실패 (토큰 누락 또는 유효하지 않음)",
                            content = @Content(schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "success": false,
                                              "message": "인증되지 않은 사용자입니다.",
                                              "status": 401
                                            }
                                            """))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "요리가 존재하지 않음",
                            content = @Content(schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "success": false,
                                              "message": "요리를 찾을 수 없습니다.",
                                              "status": 404
                                            }
                                            """))
                    )
            }
    )
    @GetMapping("/getBookmark/{id}")
    ResponseEntity<?> getBookmarkById(@Parameter(hidden = true) @AuthenticationPrincipal TokenUserInfo userInfo,
                                      @PathVariable Long id);

    @Operation(
            summary = "주문 전 사용자 정보 조회",
            description = "주문을 진행하기 위한 사용자 정보를 조회합니다. (닉네임, 연락처, 이메일, 주소 포함)",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "사용자 정보 조회 성공",
                            content = @Content(schema = @Schema(implementation = PreOrderUserResDto.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "success": true,
                                              "data": {
                                                "userId": 1,
                                                "nickname": "김소복",
                                                "phone": "01012345678",
                                                "email": "user@example.com",
                                                "addresses": [
                                                  {
                                                    "id": 12,
                                                    "roadFull": "부산시 금정구",
                                                    "addrDetail": "1층",
                                                    "defaultAddr": true
                                                  }
                                                ]
                                              },
                                              "message": "주문을 위한 사용자의 정보가 조회되었습니다.",
                                              "status": 200
                                            }
                                            """))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증 실패 (토큰 누락 또는 유효하지 않음)",
                            content = @Content(schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "success": false,
                                              "message": "인증되지 않은 사용자입니다.",
                                              "status": 401
                                            }
                                            """))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "사용자 정보 조회 실패",
                            content = @Content(schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "success": false,
                                              "message": "사용자 정보를 찾을 수 없습니다.",
                                              "status": 404
                                            }
                                            """))
                    )
            }
    )
    @GetMapping("/order-info")
    ResponseEntity<?> preOrderUser(@Parameter(hidden = true) @AuthenticationPrincipal TokenUserInfo userInfo);

    @Operation(
            summary = "사진 업로드용 URL 발급",
            description = "S3 버킷에 사진을 업로드할 수 있는 URL을 발급합니다. category는 어떤 용도(예: 프로필, 리뷰 등)인지 나타냅니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "사진 업로드용 URL 발급 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "status": 200,
                                                "message": "S3 버킷에 사진을 넣을 수 있는 URL이 성공적으로 발급되었습니다.",
                                                "data": "https://your-s3-url/presigned"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청 오류 (파일 미포함 또는 잘못된 category)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "status": 400,
                                                "message": "이미지는 필수입니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류 (S3 업로드 URL 생성 실패 등)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "status": 500,
                                                "message": "사진 업로드 URL 발급 중 오류가 발생했습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    @PatchMapping("/editPhoto/{category}")
    ResponseEntity<?> editPhoto(@Parameter(hidden = true) @AuthenticationPrincipal TokenUserInfo userInfo,
                                @RequestPart MultipartFile image,
                                @PathVariable String category);

    @Operation(
            summary = "닉네임 중복 검사",
            description = "입력한 닉네임이 사용 가능한지 확인합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "닉네임 사용 가능",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(value = """
                                                {
                                                  "success": true,
                                                  "data": null,
                                                  "message": "사용 가능한 닉네임입니다.",
                                                  "status": 200
                                                }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 (닉네임 미입력 또는 형식 오류)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(value = """
                                                {
                                                  "success": false,
                                                  "message": "닉네임을 입력해주세요.",
                                                  "status": 400
                                                }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "닉네임 중복",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(value = """
                                                {
                                                  "success": false,
                                                  "message": "이미 존재하는 닉네임입니다.",
                                                  "status": 409
                                                }
                                            """)
                            )
                    )
            }
    )
    @GetMapping("/check-nickname")
    ResponseEntity<?> checkNickname(@RequestParam String nickname);

    @Operation(
            summary = "이메일 중복 검사",
            description = "입력한 이메일이 사용 가능한지 확인합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "이메일 사용 가능",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(value = """
                                                {
                                                  "success": true,
                                                  "data": null,
                                                  "message": "사용 가능한 이메일입니다.",
                                                  "status": 200
                                                }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 (이메일 미입력 또는 형식 오류)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(value = """
                                                {
                                                  "success": false,
                                                  "message": "유효한 이메일 형식이어야 합니다.",
                                                  "status": 400
                                                }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "이메일 중복",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(value = """
                                                {
                                                  "success": false,
                                                  "message": "이미 존재하는 이메일입니다.",
                                                  "status": 409
                                                }
                                            """)
                            )
                    )
            }
    )
    @GetMapping("/check-email")
    ResponseEntity<?> checkEmail(@RequestParam String email);

    @Operation(
            summary = "게시글 좋아요 등록",
            description = "사용자가 특정 게시글에 좋아요를 등록합니다.",
            security = @SecurityRequirement(name = "bearerAuth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "좋아요 등록할 게시글 ID",
                    content = @Content(schema = @Schema(implementation = PostIdReqDto.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "좋아요 등록 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserLikeResDto.class),
                                    examples = @ExampleObject(value = """
                                                {
                                                  "success": true,
                                                  "data": {
                                                    "postId": 123,
                                                    "liked": true,
                                                    "likeCount": 56
                                                  },
                                                  "message": "좋아요 등록 성공",
                                                  "status": 200
                                                }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 (postId 누락 또는 유효하지 않은 값)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(value = """
                                                {
                                                  "success": false,
                                                  "message": "유효한 게시글 ID를 입력해주세요.",
                                                  "status": 400
                                                }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증 실패 (토큰 누락 또는 유효하지 않음)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(value = """
                                                {
                                                  "success": false,
                                                  "message": "인증되지 않은 사용자입니다.",
                                                  "status": 401
                                                }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "존재하지 않는 게시글 ID",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(value = """
                                                {
                                                  "success": false,
                                                  "message": "해당 게시글을 찾을 수 없습니다.",
                                                  "status": 404
                                                }
                                            """)
                            )
                    )
            }
    )
    @PostMapping("/user-like")
    ResponseEntity<?> likePost(@Parameter(description = "좋아요할 게시글 ID를 포함한 요청 바디", required = true)
                               @RequestBody PostIdReqDto dto,
                               @Parameter(hidden = true)
                               @AuthenticationPrincipal TokenUserInfo userInfo);

    @Operation(
            summary = "게시글 좋아요 해제",
            description = "사용자가 특정 게시글에 등록한 좋아요를 해제합니다.",
            security = @SecurityRequirement(name = "bearerAuth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "좋아요 해제할 게시글 ID",
                    content = @Content(schema = @Schema(implementation = PostIdReqDto.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "좋아요 해제 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserLikeResDto.class),
                                    examples = @ExampleObject(value = """
                                                {
                                                  "success": true,
                                                  "data": {
                                                    "postId": 123,
                                                    "liked": false,
                                                    "likeCount": 55
                                                  },
                                                  "message": "좋아요 해제 성공",
                                                  "status": 200
                                                }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 (postId 누락 또는 유효하지 않은 값)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(value = """
                                                {
                                                  "success": false,
                                                  "message": "유효한 게시글 ID를 입력해주세요.",
                                                  "status": 400
                                                }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증 실패 (토큰 누락 또는 유효하지 않음)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(value = """
                                                {
                                                  "success": false,
                                                  "message": "인증되지 않은 사용자입니다.",
                                                  "status": 401
                                                }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "존재하지 않는 게시글 ID",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(value = """
                                                {
                                                  "success": false,
                                                  "message": "해당 게시글을 찾을 수 없습니다.",
                                                  "status": 404
                                                }
                                            """)
                            )
                    )
            }
    )
    @DeleteMapping("/user-unlike")
    ResponseEntity<?> unlikePost(@Parameter(description = "좋아요 해제할 게시글 ID를 포함한 요청 바디", required = true)
                                 @RequestBody PostIdReqDto dto,
                                 @Parameter(hidden = true)
                                 @AuthenticationPrincipal TokenUserInfo userInfo);

    @Operation(
            summary = "게시글 좋아요 상태 조회",
            description = "사용자가 특정 게시글에 대해 좋아요를 눌렀는지 여부를 조회합니다.",
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(
                            name = "postId",
                            description = "조회할 게시글 ID",
                            required = true,
                            in = ParameterIn.QUERY,
                            schema = @Schema(type = "integer", format = "int64")
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "좋아요 상태 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(value = """
                                                {
                                                  "success": true,
                                                  "data": true,
                                                  "message": "해당 사용자의 좋아요 상태가 정상적으로 조회되었습니다.",
                                                  "status": 200
                                                }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 (postId 누락 또는 유효하지 않은 값)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(value = """
                                                {
                                                  "success": false,
                                                  "message": "유효한 게시글 ID를 입력해주세요.",
                                                  "status": 400
                                                }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증 실패 (토큰 누락 또는 유효하지 않음)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CommonResponse.class),
                                    examples = @ExampleObject(value = """
                                                {
                                                  "success": false,
                                                  "message": "인증되지 않은 사용자입니다.",
                                                  "status": 401
                                                }
                                            """)
                            )
                    )
            }
    )
    @GetMapping("/check-like")
    ResponseEntity<?> checkPostLike(
            @Parameter(hidden = true)
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @Parameter(description = "조회할 게시글 ID", required = true)
            @RequestParam Long postId
    );
}