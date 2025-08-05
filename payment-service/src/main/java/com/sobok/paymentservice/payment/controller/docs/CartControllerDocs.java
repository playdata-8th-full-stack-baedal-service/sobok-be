package com.sobok.paymentservice.payment.controller.docs;

import com.sobok.paymentservice.common.dto.CommonResponse;
import com.sobok.paymentservice.common.dto.TokenUserInfo;
import com.sobok.paymentservice.payment.dto.cart.DeleteCartReqDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "장바구니 관리 (CartController)", description = "장바구니 관련 기능 제공")
@RequestMapping("/cart")
public interface CartControllerDocs {
    @Operation(
            summary = "장바구니 요리 수량 수정",
            description = "장바구니에 담긴 특정 요리의 수량을 수정합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "장바구니 수량 변경 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "수량 변경 성공 예시",
                                    value = """
                                            {
                                              "success": true,
                                              "status": 200,
                                              "message": "장바구니 요리 수량 변경이 정상적으로 처리되었습니다.",
                                              "data": 12
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (유효하지 않은 수량 등)",
                    content = @Content(
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "status": 400,
                                              "message": "수량은 1 이상이어야 합니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 장바구니 요리 ID",
                    content = @Content(
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "status": 404,
                                              "message": "해당하는 장바구니 요리가 없습니다.",
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
                                              "message": "인증되지 않은 사용자입니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @PatchMapping("/{id}")
    ResponseEntity<?> editCartCookCount(
            @Parameter(hidden = true) @AuthenticationPrincipal TokenUserInfo userInfo,
            @Parameter(description = "장바구니 요리 ID", example = "12") @PathVariable(name = "id") Long cartCookId,
            @Parameter(description = "변경할 수량", example = "2") @RequestParam Integer count
    );

    @Operation(
            summary = "장바구니 요리 삭제",
            description = "장바구니에 담긴 특정 요리를 삭제합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "장바구니 요리 삭제 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "삭제 성공 예시",
                                    value = """
                                            {
                                              "success": true,
                                              "status": 200,
                                              "message": "장바구니 요리 삭제가 정상적으로 처리되었습니다.",
                                              "data": 12
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 장바구니 요리 ID",
                    content = @Content(
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "status": 404,
                                              "message": "해당 장바구니 항목을 찾을 수 없습니다.",
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
    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteCartCook(
            @Parameter(hidden = true) @AuthenticationPrincipal TokenUserInfo userInfo,
            @Parameter(description = "장바구니 요리 ID", example = "12") @PathVariable(name = "id") Long cartCookId
    );

    @Operation(
            summary = "장바구니 다중 요리 삭제",
            description = "장바구니에 담긴 여러 요리를 한 번에 삭제합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "장바구니 다중 요리 삭제 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "삭제 성공 예시",
                                    value = """
                                            {
                                              "success": true,
                                              "status": 200,
                                              "message": "장바구니의 모든 요리 제거가 성공적으로 처리되었습니다.",
                                              "data": 5
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 요청 (빈 리스트 또는 잘못된 형식)",
                    content = @Content(
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "status": 400,
                                              "message": "삭제할 항목이 없습니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "결제 완료된 장바구니 항목에 대한 접근",
                    content = @Content(
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "status": 403,
                                              "message": "잘못된 접근입니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 장바구니 요리 ID 포함",
                    content = @Content(
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "status": 404,
                                              "message": "장바구니 요리 항목 중 일부를 찾을 수 없습니다.",
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
    @DeleteMapping("/all")
    ResponseEntity<?> deleteCart(
            @Parameter(hidden = true) @AuthenticationPrincipal TokenUserInfo userInfo,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "삭제할 장바구니 요리 ID 리스트",
                    required = true,
                    content = @Content(schema = @Schema(implementation = DeleteCartReqDto.class))
            )
            @RequestBody DeleteCartReqDto reqDto
    );
}
