package com.sobok.apiservice.api.controller.docs;

import com.sobok.apiservice.api.dto.google.GoogleCallResDto;
import com.sobok.apiservice.api.dto.kakao.KakaoCallResDto;
import com.sobok.apiservice.common.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "소셜 로그인", description = "카카오/구글 소셜 로그인 관련 API")
public interface SocialLoginControllerDocs {
    @Operation(
            summary = "카카오 로그인 콜백",
            description = "카카오 인증 후 전달된 code로 로그인 및 회원가입 처리 후 로그인 정보를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인/회원가입 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = KakaoCallResDto.class),
                            examples = @ExampleObject(
                                    name = "카카오 로그인 성공 응답 예시",
                                    summary = "회원가입 또는 로그인 성공 시 반환되는 유저 정보",
                                    value = """
                                            {
                                              "oauthId": 12345678,
                                              "authId": 987654321,
                                              "newUser": true,
                                              "nickname": "홍길동",
                                              "profileImage": "https://k.kakaocdn.net/dn/abc123.jpg",
                                              "email": "honggildong@example.com",
                                              "new": true
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    void kakaoCallback(
            @Parameter(
                    description = "카카오 인증 서버에서 전달하는 인가 코드",
                    required = true,
                    in = ParameterIn.QUERY
            )
            @RequestParam String code,
            HttpServletResponse response
    ) throws java.io.IOException;


    @Operation(
            summary = "구글 로그인 콜백",
            description = "구글 인증 후 전달된 code로 로그인 및 회원가입 처리 후 로그인 정보를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인/회원가입 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GoogleCallResDto.class),
                            examples = @ExampleObject(
                                    name = "구글 로그인 성공 응답 예시",
                                    summary = "회원가입 또는 로그인 성공 시 반환되는 유저 정보",
                                    value = """
                                            {
                                              "oauthId": 123456789,
                                              "authId": 987654321,
                                              "email": "honggildong@example.com",
                                              "name": "홍길동",
                                              "picture": "https://example.com/image.jpg",
                                              "nickname": "홍길동",
                                              "isNew": true
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    void googleCallback(
            @Parameter(
                    description = "구글 인증 서버에서 전달하는 인가 코드",
                    required = true,
                    in = ParameterIn.QUERY
            )
            @RequestParam String code,
            HttpServletResponse response
    ) throws java.io.IOException;


    @Operation(summary = "구글 로그인 페이지 URL 조회", description = "구글 로그인에 사용할 인증 URL을 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "구글 로그인 URL 반환",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "success": true,
                                      "data": "https://accounts.google.com/o/oauth2/v2/auth?client_id=XXX&redirect_uri=YYY&response_type=code&scope=email%20profile%20openid&access_type=offline",
                                      "message": "google login view url입니다.",
                                      "status": 200
                                    }
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    ResponseEntity<?> getGoogleLoginView();
}