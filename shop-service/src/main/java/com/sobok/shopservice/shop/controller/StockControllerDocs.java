package com.sobok.shopservice.shop.controller;

import com.sobok.shopservice.common.dto.CommonResponse;
import com.sobok.shopservice.common.dto.TokenUserInfo;
import com.sobok.shopservice.shop.dto.stock.StockReqDto;
import com.sobok.shopservice.shop.dto.stock.StockResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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

@Tag(name = "가게 재고 관리 (StockController)", description = "가게 재고 관련 기능 제공")
@RequestMapping("/stock")
public interface StockControllerDocs {

    @Operation(
            summary = "식재료 재고 등록",
            description = """
                    인증된 사용자의 가게에 식재료 재고를 등록합니다.
                    
                    ### 요청 형식
                    - 인증 토큰 필요
                    - 요청 본문에 재고 등록 정보 포함
                    
                    ### 응답 형식
                    - 200 OK: 재고 등록 성공 시 등록된 재고 정보 반환
                    - 400 Bad Request: 요청 데이터 검증 실패 (필수 필드 누락, 형식 오류 등)
                    - 401 Unauthorized: 인증 실패
                    """
            ,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "재고 등록 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StockResDto.class),
                            examples = @ExampleObject(
                                    name = "재고 등록 성공 예시",
                                    value = """
                                            {
                                              "success": true,
                                              "status": 200,
                                              "message": "식재료 재고 등록이 정상적으로 처리되었습니다.",
                                              "data": {
                                                "stockId": 123,
                                                "shopId": 10,
                                                "ingredientId": 45,
                                                "quantity": 50
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (유효성 검증 실패)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "유효성 검증 실패 예시",
                                    value = """
                                            {
                                              "success": false,
                                              "status": 400,
                                              "message": "재고 수량은 0 이상이어야 합니다.",
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
                                              "message": "인증 정보가 유효하지 않습니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping()
    ResponseEntity<?> registerStock(
            @Parameter(hidden = true) @AuthenticationPrincipal TokenUserInfo userInfo,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "등록할 식재료 재고 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = StockReqDto.class))
            )
            @Valid @RequestBody StockReqDto reqDto
    );

    @Operation(
            summary = "식재료 재고 사용 (차감)",
            description = """
                    인증된 사용자의 가게 재고에서 지정한 식재료 수량을 차감합니다.
                    
                    ### 요청 형식
                    - 인증 토큰 필요
                    - 요청 본문에 재고 차감 정보 포함 (ingredientId, quantity)
                    
                    ### 응답 형식
                    - 200 OK: 재고 차감 성공 시 차감 후 재고 정보 반환
                    - 400 Bad Request: 요청 데이터 검증 실패 (필수 필드 누락, 수량 오류 등)
                    - 401 Unauthorized: 인증 실패
                    - 409 Conflict: 재고 부족 등 비즈니스 로직 오류
                    """
            ,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "재고 차감 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StockResDto.class),
                            examples = @ExampleObject(
                                    name = "재고 차감 성공 예시",
                                    value = """
                                            {
                                              "success": true,
                                              "status": 200,
                                              "message": "식재료 재고 사용이 정상적으로 처리되었습니다.",
                                              "data": {
                                                "id": 123,
                                                "shopId": 10,
                                                "ingredientId": 45,
                                                "quantity": 30
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (유효성 검증 실패)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "유효성 검증 실패 예시",
                                    value = """
                                            {
                                              "success": false,
                                              "status": 400,
                                              "message": "재고 수량은 0 이상이어야 합니다.",
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
                                              "message": "인증 정보가 유효하지 않습니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "재고 부족 등 비즈니스 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "재고 부족 예시",
                                    value = """
                                            {
                                              "success": false,
                                              "status": 409,
                                              "message": "재고가 부족합니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @PatchMapping()
    ResponseEntity<?> deductStock(
            @Parameter(hidden = true) @AuthenticationPrincipal TokenUserInfo userInfo,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "차감할 재고 정보 (ingredientId, quantity)",
                    required = true,
                    content = @Content(schema = @Schema(implementation = StockReqDto.class))
            )
            @Valid @RequestBody StockReqDto reqDto
    );

    @Operation(
            summary = "가게의 모든 식재료 재고 조회",
            description = """
                    인증된 사용자의 권한을 확인한 후, 지정한 가게의 모든 식재료 재고 정보를 조회합니다.
                    
                    ### 요청 형식
                    - 인증 토큰 필요
                    - 경로 변수로 가게 ID 전달
                    
                    ### 응답 형식
                    - 200 OK: 가게의 식재료 재고 리스트 반환
                    - 401 Unauthorized: 인증 실패
                    - 403 Forbidden: 권한 없음 (사용자와 요청 가게 불일치 등)
                    - 404 Not Found: 가게 또는 재고 정보 없음
                    """
            ,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "재고 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = StockResDto.class)),
                            examples = @ExampleObject(
                                    name = "재고 목록 조회 성공 예시",
                                    value = """
                                            {
                                              "success": true,
                                              "status": 200,
                                              "message": "가게의 모든 식재료 재고 정보를 성공적으로 조회하였습니다.",
                                              "data": [
                                                {
                                                  "id": 123,
                                                  "shopId": 10,
                                                  "ingredientId": 45,
                                                  "quantity": 50
                                                },
                                                {
                                                  "id": 124,
                                                  "shopId": 10,
                                                  "ingredientId": 46,
                                                  "quantity": 20
                                                }
                                              ]
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
                                              "message": "인증 정보가 유효하지 않습니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "권한 없음 예시",
                                    value = """
                                            {
                                              "success": false,
                                              "status": 403,
                                              "message": "해당 가게에 대한 접근 권한이 없습니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "가게 또는 재고 정보 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "가게 또는 재고 없음 예시",
                                    value = """
                                            {
                                              "success": false,
                                              "status": 404,
                                              "message": "해당 가게 또는 재고 정보를 찾을 수 없습니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/{id}")
    ResponseEntity<?> getStock(
            @Parameter(hidden = true) @AuthenticationPrincipal TokenUserInfo userInfo,
            @Parameter(description = "조회할 가게 ID", required = true, example = "10")
            @PathVariable("id") Long shopId

    );

    @Operation(
            summary = "본인 가게의 모든 식재료 재고 조회",
            description = """
                    인증된 사용자의 가게 ID를 기반으로 모든 식재료 재고 정보를 조회합니다.
                    
                    ### 요청 형식
                    - 인증 토큰 필요
                    - 별도의 파라미터 없음 (인증된 사용자 정보에서 shopId 사용)
                    
                    ### 응답 형식
                    - 200 OK: 가게의 식재료 재고 리스트 반환
                    - 401 Unauthorized: 인증 실패
                    - 404 Not Found: 재고 정보가 없을 경우 (필요 시)
                    """
            ,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "재고 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = StockResDto.class)),
                            examples = @ExampleObject(
                                    name = "재고 목록 조회 성공 예시",
                                    value = """
                                            {
                                              "success": true,
                                              "status": 200,
                                              "message": "가게의 모든 식재료 재고 정보를 성공적으로 조회하였습니다.",
                                              "data": [
                                                {
                                                  "id": 123,
                                                  "shopId": 10,
                                                  "ingredientId": 45,
                                                  "quantity": 50
                                                },
                                                {
                                                  "id": 124,
                                                  "shopId": 10,
                                                  "ingredientId": 46,
                                                  "quantity": 20
                                                }
                                              ]
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
                                              "message": "인증 정보가 유효하지 않습니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping()
    ResponseEntity<?> getStock(
            @Parameter(hidden = true) @AuthenticationPrincipal TokenUserInfo userInfo
    );
}
