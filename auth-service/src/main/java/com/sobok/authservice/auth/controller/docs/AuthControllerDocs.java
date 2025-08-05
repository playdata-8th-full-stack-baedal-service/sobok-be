package com.sobok.authservice.auth.controller.docs;

import com.sobok.authservice.auth.dto.request.*;
import com.sobok.authservice.common.dto.CommonResponse;
import com.sobok.authservice.common.dto.TokenUserInfo;
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
import org.springframework.web.bind.annotation.*;

@Tag(name = "AuthController", description = "사용자, 라이더, 가게 회원가입, 로그인, 정보 조회, 상태 관리 API")
@RequestMapping("/auth")
public interface AuthControllerDocs {

    @Operation(summary = "사용자 회원가입", description = "사용자 신규 회원가입 요청")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "success": true,
                                        "data": {
                                            "id": 1,
                                            "loginId": "user123",
                                            "email": "user@example.com"
                                        },
                                        "message": "회원가입 성공",
                                        "status": 200
                                    }
                                    """)
                    )),
            @ApiResponse(responseCode = "400", description = "입력값 오류 또는 중복",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "success": false,
                                        "data": null,
                                        "message": "이미 사용 중인 아이디입니다.",
                                        "status": 400
                                    }
                                    """)
                    ))
    })
    ResponseEntity<?> createAuth(
            @Parameter(description = "회원가입 요청 DTO", required = true)
            @Valid @RequestBody AuthUserReqDto authUserReqDto);

    @Operation(summary = "아이디 중복 확인", description = "로그인 아이디 중복 여부 확인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용 가능한 아이디",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "success": true,
                                        "data": null,
                                        "message": "사용 가능한 아이디입니다.",
                                        "status": 200
                                    }
                                    """)
                    )),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 이미 존재",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "success": false,
                                        "data": null,
                                        "message": "이미 사용 중인 아이디입니다.",
                                        "status": 400
                                    }
                                    """)
                    ))
    })
    ResponseEntity<?> checkLoginId(
            @Parameter(description = "중복 확인할 로그인 아이디", required = true)
            @RequestParam String loginId);

    @Operation(summary = "임시 토큰 발급", description = "임시 토큰을 발급합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "임시 토큰 발급 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "success": true,
                                        "data": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                        "message": "임시 토큰이 발급되었습니다.",
                                        "status": 200
                                    }
                                    """)
                    ))
    })
    ResponseEntity<?> getTempToken();

    @Operation(summary = "통합 로그인", description = "아이디, 비밀번호로 로그인 처리")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "success": true,
                                        "data": {
                                            "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                            "refreshToken": "d8f3e6c4-xxx-xxxx-xxxx",
                                            "recoveryTarget": false
                                        },
                                        "message": "로그인에 성공하였습니다.",
                                        "status": 200
                                    }
                                    """)
                    )),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "success": false,
                                        "data": null,
                                        "message": "로그인 정보가 올바르지 않습니다.",
                                        "status": 401
                                    }
                                    """)
                    ))
    })
    ResponseEntity<?> login(
            @Parameter(description = "로그인 요청 DTO", required = true)
            @Valid @RequestBody AuthLoginReqDto reqDto);

    @Operation(summary = "통합 로그아웃", description = "로그아웃 처리",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "success": true,
                                        "data": 1,
                                        "message": "로그아웃에 성공하였습니다.",
                                        "status": 200
                                    }
                                    """)
                    )),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "success": false,
                                        "data": null,
                                        "message": "인증에 실패하였습니다.",
                                        "status": 401
                                    }
                                    """)
                    ))
    })
    ResponseEntity<?> logout(
            @RequestHeader("Authorization") String authorizationHeader,
            @Parameter(hidden = true) TokenUserInfo userInfo);

    @Operation(summary = "토큰 재발급", description = "만료 토큰 재발급")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "재발급 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "success": true,
                                        "data": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                        "message": "토큰이 성공적으로 발급되었습니다.",
                                        "status": 200
                                    }
                                    """)
                    )),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "success": false,
                                        "data": null,
                                        "message": "잘못된 토큰 요청입니다.",
                                        "status": 400
                                    }
                                    """)
                    ))
    })
    ResponseEntity<?> reissue(
            @Parameter(description = "재발급 요청 DTO", required = true)
            @RequestBody AuthReissueReqDto reqDto);

    @Operation(summary = "비밀번호 검증", description = "현재 비밀번호 확인",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검증 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "success": true,
                                        "data": null,
                                        "message": "비밀번호가 확인되었습니다.",
                                        "status": 200
                                    }
                                    """)
                    )),
            @ApiResponse(responseCode = "400", description = "비밀번호 불일치",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "success": false,
                                        "data": null,
                                        "message": "비밀번호가 일치하지 않습니다.",
                                        "status": 400
                                    }
                                    """)
                    ))
    })
    ResponseEntity<?> verifyPassword(
            @Parameter(hidden = true) TokenUserInfo userInfo,
            @Parameter(description = "비밀번호 검증 DTO", required = true)
            @RequestBody AuthPasswordReqDto reqDto);

    @Operation(summary = "사용자 비활성화 (탈퇴)", description = "계정 비활성화 처리",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "비활성화 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "success": true,
                                        "data": 1,
                                        "message": "사용자가 정상적으로 비활성화되었습니다.",
                                        "status": 200
                                    }
                                    """)
                    )),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "success": false,
                                        "data": null,
                                        "message": "인증에 실패하였습니다.",
                                        "status": 401
                                    }
                                    """)
                    ))
    })
    ResponseEntity<?> delete(
            @RequestHeader("Authorization") String authorizationHeader,
            @Parameter(hidden = true) TokenUserInfo userInfo);

    @Operation(summary = "사용자 복구", description = "비활성화된 사용자 계정을 비밀번호 확인 후 복구합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "복구 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "success": true,
                                        "data": 1,
                                        "message": "사용자의 계정이 정상적으로 복구되었습니다.",
                                        "status": 200
                                    }
                                    """)
                    )),
            @ApiResponse(responseCode = "400", description = "비밀번호 불일치 또는 잘못된 요청",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "success": false,
                                        "data": null,
                                        "message": "비밀번호가 일치하지 않습니다.",
                                        "status": 400
                                    }
                                    """)
                    )),
            @ApiResponse(responseCode = "404", description = "사용자 없음 또는 복구 대상 아님",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "success": false,
                                        "data": null,
                                        "message": "복구 대상이 아닌 계정입니다.",
                                        "status": 404
                                    }
                                    """)
                    ))
    })
    ResponseEntity<?> recover(
            @Parameter(description = "복구할 사용자 ID", required = true)
            @PathVariable Long id,
            @RequestBody RecoverReqDto reqDto);


    @Operation(summary = "라이더 회원가입", description = "새 라이더 회원가입")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "success": true,
                                        "data": {
                                            "id": 10,
                                            "name": "홍길동"
                                        },
                                        "message": "라이더 회원가입 성공",
                                        "status": 200
                                    }
                                    """)
                    )),
            @ApiResponse(responseCode = "400", description = "입력 오류",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "success": false,
                                        "data": null,
                                        "message": "입력값이 올바르지 않습니다.",
                                        "status": 400
                                    }
                                    """)
                    ))
    })
    ResponseEntity<?> createRider(
            @Parameter(description = "라이더 회원가입 요청 DTO", required = true)
            @Valid @RequestBody AuthRiderReqDto authRiderReqDto);

    @Operation(summary = "가게 회원가입", description = "새 가게 회원가입",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "success": true,
                                        "data": {
                                            "id": 20,
                                            "shopName": "맛집",
                                            "ownerName": "김사장"
                                        },
                                        "message": "가게 회원가입 성공",
                                        "status": 200
                                    }
                                    """)
                    )),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "success": false,
                                        "data": null,
                                        "message": "인증이 필요합니다.",
                                        "status": 401
                                    }
                                    """)
                    )),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "success": false,
                                        "data": null,
                                        "message": "권한이 없습니다.",
                                        "status": 403
                                    }
                                    """)
                    ))
    })
    ResponseEntity<?> createShop(
            @Parameter(description = "가게 회원가입 요청 DTO", required = true)
            @Valid @RequestBody AuthShopReqDto authShopReqDto,
            @Parameter(hidden = true) TokenUserInfo userInfo);

    @Operation(summary = "사용자 아이디 찾기", description = "전화번호와 인증번호로 아이디 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "아이디 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "success": true,
                                        "data": "user123",
                                        "message": "아이디를 찾았습니다.",
                                        "status": 200
                                    }
                                    """)
                    )),
            @ApiResponse(responseCode = "404", description = "사용자 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "success": false,
                                        "data": null,
                                        "message": "해당 사용자가 존재하지 않습니다.",
                                        "status": 404
                                    }
                                    """)
                    ))
    })
    ResponseEntity<?> getFindUserId(
            @Parameter(description = "아이디 찾기 요청 DTO", required = true)
            @RequestBody AuthFindIdReqDto authFindReqDto);

    @Operation(summary = "비밀번호 찾기 - 인증번호 발송", description = "비밀번호 찾기 인증번호 요청")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증번호 발송 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "success": true,
                                        "data": null,
                                        "message": "인증번호가 발송되었습니다.",
                                        "status": 200
                                    }
                                    """)
                    )),
            @ApiResponse(responseCode = "400", description = "입력 오류",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "success": false,
                                        "data": null,
                                        "message": "잘못된 입력입니다.",
                                        "status": 400
                                    }
                                    """)
                    ))
    })
    ResponseEntity<?> authVerification(
            @Parameter(description = "인증번호 요청 DTO", required = true)
            @RequestBody AuthVerifyReqDto authVerifyReqDto);

    @Operation(summary = "비밀번호 재설정", description = "임시 비밀번호로 재설정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "비밀번호 재설정 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "success": true,
                                        "data": null,
                                        "message": "비밀번호가 재설정되었습니다.",
                                        "status": 200
                                    }
                                    """)
                    )),
            @ApiResponse(responseCode = "404", description = "사용자 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "success": false,
                                        "data": null,
                                        "message": "사용자를 찾을 수 없습니다.",
                                        "status": 404
                                    }
                                    """)
                    ))
    })
    ResponseEntity<?> resetPassword(
            @Parameter(description = "비밀번호 재설정 DTO", required = true)
            @RequestBody AuthResetPwReqDto authResetPwReqDto);

    @Operation(summary = "비밀번호 변경", description = "로그인 사용자 비밀번호 변경",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "success": true,
                                        "data": 1,
                                        "message": "비밀번호가 변경되었습니다.",
                                        "status": 200
                                    }
                                    """)
                    )),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "success": false,
                                        "data": null,
                                        "message": "인증에 실패하였습니다.",
                                        "status": 401
                                    }
                                    """)
                    ))
    })
    ResponseEntity<?> editPassword(
            @Parameter(hidden = true) TokenUserInfo userInfo,
            @Parameter(description = "비밀번호 변경 DTO", required = true)
            @Valid @RequestBody AuthEditPwReqDto authEditPwReqDto);

    @Operation(summary = "회원 정보 조회", description = "로그인 사용자 정보 조회",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "success": true,
                                        "data": {
                                            "id": 1,
                                            "loginId": "user123",
                                            "email": "user@example.com",
                                            "role": "USER"
                                        },
                                        "message": "회원 정보를 조회하였습니다.",
                                        "status": 200
                                    }
                                    """)
                    ))
    })
    ResponseEntity<?> getInfo(
            @Parameter(hidden = true) TokenUserInfo userInfo);

    @Operation(summary = "소셜 회원가입", description = "OAuth 기반 소셜 회원가입")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "success": true,
                                        "data": {
                                            "id": 2,
                                            "socialId": "google_12345"
                                        },
                                        "message": "소셜 회원가입에 성공하였습니다.",
                                        "status": 200
                                    }
                                    """)
                    ))
    })
    ResponseEntity<?> createSocialAuth(
            @Parameter(description = "소셜 회원가입 DTO", required = true)
            @Valid @RequestBody AuthByOauthReqDto authByOauthReqDto);

    @Operation(summary = "라이더 회원가입 승인 요청", description = "라이더 계정 활성화 처리")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "라이더 활성화 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "success": true,
                                        "data": null,
                                        "message": "라이더가 성공적으로 활성화되었습니다.",
                                        "status": 200
                                    }
                                    """)
                    ))
    })
    ResponseEntity<CommonResponse<Void>> activeRider(
            @Parameter(description = "활성화할 라이더 ID", required = true)
            @RequestParam Long riderId);

}
