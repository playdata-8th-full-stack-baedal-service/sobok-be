package com.sobok.apiservice.api.controller.docs;

import com.sobok.apiservice.api.dto.toss.TossPayReqDto;
import com.sobok.apiservice.api.dto.toss.TossPayResDto;
import com.sobok.apiservice.common.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "토스페이 결제", description = "토스페이 결제 관련 API")
public interface TossControllerDocs {

    @Operation(
            summary = "토스페이 결제 요청",
            description = "토스페이 결제를 승인하고 주문 정보를 등록합니다.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "결제 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "success": true,
                                      "data": {
                                        "orderId": "ORDER_12345",
                                        "paymentKey": "tk_abcdefgh123456789",
                                        "method": "카드"
                                      },
                                      "message": "정상 처리되었습니다.",
                                      "status": 200
                                    }
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 결제 검증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "success": false,
                                      "data": null,
                                      "message": "잘못된 요청입니다.",
                                      "status": 400
                                    }
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "success": false,
                                      "data": null,
                                      "message": "서버 내부 오류가 발생했습니다.",
                                      "status": 500
                                    }
                                    """)
                    )
            )
    })
    @PostMapping("/confirm")
    ResponseEntity<?> confirmPayment(@RequestBody TossPayReqDto reqDto);
}
