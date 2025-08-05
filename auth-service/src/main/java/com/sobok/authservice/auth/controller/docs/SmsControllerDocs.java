package com.sobok.authservice.auth.controller.docs;

import com.sobok.authservice.auth.dto.request.SmsReqDto;
import com.sobok.authservice.auth.dto.request.VerificationReqDto;
import com.sobok.authservice.common.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;

public interface SmsControllerDocs {

    @Operation(summary = "문자 인증번호 전송", description = "입력된 전화번호로 인증번호를 문자로 발송합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "문자 전송 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "success": true,
                                        "data": null,
                                        "message": "문자를 전송했습니다.",
                                        "status": 200
                                    }
                                    """)
                    )),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (번호 형식 오류 등)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "success": false,
                                        "data": null,
                                        "message": "인증번호 전송에 실패하였습니다.",
                                        "status": 400
                                    }
                                    """)
                    ))
    })
    ResponseEntity<?> SendSMS(
            @Parameter(description = "전화번호 입력 DTO", required = true)
            @RequestBody @Valid SmsReqDto smsReqDto);

    @Operation(summary = "문자 인증번호 검증", description = "전화번호와 인증번호를 비교하여 인증을 검증합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "success": true,
                                        "data": null,
                                        "message": "인증 성공",
                                        "status": 200
                                    }
                                    """)
                    )),
            @ApiResponse(responseCode = "400", description = "인증 실패 (코드 불일치 또는 만료)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "success": false,
                                        "data": null,
                                        "message": "인증번호가 일치하지 않습니다.",
                                        "status": 400
                                    }
                                    """)
                    ))
    })
    ResponseEntity<?> verifyCode(
            @Parameter(description = "전화번호 + 인증코드 DTO", required = true)
            @RequestBody @Valid VerificationReqDto request);

}
