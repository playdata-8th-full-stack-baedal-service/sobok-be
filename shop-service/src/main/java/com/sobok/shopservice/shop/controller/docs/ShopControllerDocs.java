package com.sobok.shopservice.shop.controller.docs;

import com.sobok.shopservice.common.dto.CommonResponse;
import com.sobok.shopservice.common.dto.TokenUserInfo;
import com.sobok.shopservice.shop.dto.response.ShopPaymentResDto;
import com.sobok.shopservice.shop.dto.response.ShopResDto;
import com.sobok.shopservice.shop.dto.stock.AvailableShopInfoDto;
import com.sobok.shopservice.shop.dto.stock.IngredientIdListDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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

@Tag(name = "가게 관리 (ShopController)", description = "가게 관련 기능 제공")
@RequestMapping("/shop")
public interface ShopControllerDocs {
    @Operation(
            summary = "지점명 중복 확인",
            description = """
                    입력한 지점명(shopName)의 중복 여부를 확인합니다.
                    ### 요청 형식
                    - 쿼리 파라미터로 `shopName`을 전달해야 합니다.
                    - 로그인한 사용자의 토큰 필요 (보안 인증 필요)
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "사용 가능한 지점명",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "지점명 사용 가능 예시",
                                    value = """
                                            {
                                              "success": true,
                                              "status": 200,
                                              "message": "사용 가능한 지점명 입니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청 파라미터 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "잘못된 요청 예시",
                                    value = """
                                            {
                                              "success": false,
                                              "status": 400,
                                              "message": "지점명이 비어있거나 유효하지 않습니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "중복된 지점명",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "지점명 중복 예시",
                                    value = """
                                            {
                                              "success": false,
                                              "status": 400,
                                              "message": "이미 등록된 지점명 입니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 (토큰 누락 또는 만료)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "인증 실패 예시",
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
    @GetMapping("/check-shopName")
    ResponseEntity<?> checkShopName(@RequestParam String shopName);

    @Operation(
            summary = "지점 주소 중복 확인",
            description = """
                    입력한 지점 주소(shopAddress)가 사용 가능한지 확인합니다.
                    ### 요청 형식
                    - 쿼리 파라미터로 shopAddress 전달
                    - 로그인(인증) 필요할 경우 JWT 토큰 필요
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "사용 가능한 주소",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": true,
                                              "status": 200,
                                              "message": "사용 가능한 주소 입니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 파라미터",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "status": 400,
                                              "message": "shopAddress가 누락되었거나 형식이 올바르지 않습니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "중복된 주소",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "status": 400,
                                              "message": "중복된 가게 주소 입니다.",
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
    @GetMapping("/check-shopAddress")
    ResponseEntity<?> checkShopAddress(@RequestParam String shopAddress);

    @Operation(
            summary = "모든 주문 목록 조회",
            description = """
                    로그인한 사용자의 모든 주문 목록을 조회합니다.
                    ### 요청 형식
                    - 인증된 사용자 토큰 필요
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "주문 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ShopPaymentResDto.class),
                            examples = @ExampleObject(
                                    name = "주문 목록 조회 성공 예시",
                                    value = """
                                            {
                                              "success": true,
                                              "status": 200,
                                              "message": "들어온 모든 주문 목록을 조회하였습니다.",
                                              "data": [
                                                {
                                                  "paymentId": 1001,
                                                  "orderId": "ORD123456",
                                                  "orderState": "DELIVERY_COMPLETE",
                                                  "updatedAt": "2025-08-04T15:30:00"
                                                },
                                                {
                                                  "paymentId": 1002,
                                                  "orderId": "ORD123457",
                                                  "orderState": "PREPARING_INGREDIENTS",
                                                  "updatedAt": "2025-08-04T15:45:00"
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "주문 목록이 존재하지 않음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "주문 없음 예시",
                                    value = """
                                            {
                                              "success": true,
                                              "status": 204,
                                              "message": "주문 목록이 존재하지 않습니다.",
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
                                              "message": "인증되지 않은 사용자입니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/all-order")
    ResponseEntity<?> getAllOrders(@AuthenticationPrincipal TokenUserInfo userInfo);

    @Operation(
            summary = "주문 상태별 필터링 조회",
            description = """
                    로그인한 사용자의 주문 목록을 주문 상태(orderState) 기준으로 필터링하여 조회합니다.
                    ### 요청 형식
                    - 인증된 사용자 토큰 필요
                    - 쿼리 파라미터로 유효한 orderState 값 전달 필요
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "주문 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ShopPaymentResDto.class),
                            examples = @ExampleObject(
                                    name = "주문 목록 조회 성공 예시",
                                    value = """
                                            {
                                              "success": true,
                                              "status": 200,
                                              "message": "주문 목록을 상태별로 조회하였습니다.",
                                              "data": [
                                                {
                                                  "paymentId": 1001,
                                                  "orderId": "ORD123456",
                                                  "orderState": "PREPARING_INGREDIENTS",
                                                  "updatedAt": "2025-08-04T15:30:00"
                                                },
                                                {
                                                  "paymentId": 1002,
                                                  "orderId": "ORD123457",
                                                  "orderState": "DELIVERY_COMPLETE",
                                                  "updatedAt": "2025-08-04T15:45:00"
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "해당 상태의 주문이 존재하지 않음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "주문 없음 예시",
                                    value = """
                                            {
                                              "success": true,
                                              "status": 204,
                                              "message": "해당 상태의 주문이 존재하지 않습니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 주문 상태값",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "잘못된 상태값 예시",
                                    value = """
                                            {
                                              "success": false,
                                              "status": 400,
                                              "message": "주문 상태값이 올바르지 않습니다.",
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
                                              "message": "인증되지 않은 사용자입니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/filtering-order")
    ResponseEntity<?> getFilterOrders(
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @Parameter(description = "조회할 주문 상태값", required = true, example = "PREPARING_INGREDIENTS")
            @RequestParam String orderState
    );

    @Operation(
            summary = "가능한 가게 조회",
            description = """
                    사용자의 주소 ID와 재료 목록을 기반으로 주문 가능한 가게 목록을 조회합니다.
                    ### 요청 형식
                    - 주소 ID는 쿼리 파라미터로 전달
                    - 요청 본문에는 재료 ID, 수량, 가게 ID가 포함된 리스트 전달
                    """,
            parameters = {
                    @Parameter(
                            name = "addressId",
                            description = "사용자의 주소 ID",
                            required = true,
                            in = ParameterIn.QUERY,
                            example = "1"
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "재료 ID 및 수량 정보 리스트",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AvailableShopInfoDto.class),
                            examples = @ExampleObject(
                                    name = "요청 예시",
                                    value = """
                                            {
                                              "cartIngredientStockList": [
                                                {
                                                  "shopId": 10,
                                                  "ingredientId": 1,
                                                  "quantity": 2
                                                },
                                                {
                                                  "shopId": 10,
                                                  "ingredientId": 2,
                                                  "quantity": 1
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "가능한 가게 정보를 성공적으로 조회하였습니다.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "응답 예시",
                                    value = """
                                            {
                                              "success": true,
                                              "data": [
                                                {
                                                  "shopId": 10,
                                                  "shopName": "도봉지점",
                                                  "cartIngredientStockList": [
                                                    {
                                                      "shopId": 10,
                                                      "ingredientId": 1,
                                                      "quantity": 10
                                                    },
                                                    {
                                                      "shopId": 10,
                                                      "ingredientId": 2,
                                                      "quantity": 20
                                                    }
                                                  ],
                                                  "satisfiable": false,
                                                  "missingIngredients": [
                                                    {
                                                      "ingredientId": 10,
                                                      "ingredientName": "우유",
                                                      "quantity": 0
                                                    }
                                                  ]
                                                }
                                              ],
                                              "message": "가능한 가게 정보를 성공적으로 조회하였습니다.",
                                              "status": 200
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청 데이터 오류 (예: addressId 없음, 형식 불일치 등)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "오류 예시",
                                    value = """
                                            {
                                              "success": false,
                                              "message": "addressId는 필수입니다.",
                                              "status": 400,
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "주소를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "주소 없음 예시",
                                    value = """
                                            {
                                              "success": false,
                                              "status": 404,
                                              "message": "주소를 찾을 수 없습니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/available")
    ResponseEntity<?> getAvailableShopList(
            @RequestParam Long addressId,
            @RequestBody IngredientIdListDto reqDto
    );

    @Operation(
            summary = "전체 가게 조회",
            description = """
                    설정한 주소를 기준으로 주문 가능한 가게 목록을 조회합니다.
                    ### 요청 형식
                    - 인증된 사용자 토큰 필요
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "전체 가게 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ShopResDto.class),
                            examples = @ExampleObject(
                                    name = "전체 가게 조회 성공 예시",
                                    value = """
                                            {
                                              "success": true,
                                              "status": 200,
                                              "message": "가게 전체 조회 성공",
                                              "data": [
                                                {
                                                  "id": 1,
                                                  "shopName": "소복 강남점",
                                                  "roadFull": "서울시 도봉구 도봉로 123",
                                                  "ownerName": "김소복",
                                                  "phone": "01012345678"
                                                },
                                                {
                                                  "id": 2,
                                                  "shopName": "소복 서초점",
                                                  "roadFull": "서울시 도봉구 방학로 456",
                                                  "ownerName": "박소복",
                                                  "phone": "01098765432"
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
                                              "message": "인증되지 않은 사용자입니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음 (관리자만 접근 가능)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "권한 없음 예시",
                                    value = """
                                            {
                                              "success": false,
                                              "status": 403,
                                              "message": "접근 권한이 없습니다. 관리자만 접근 가능합니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/all")
    ResponseEntity<?> getAllShops(@AuthenticationPrincipal TokenUserInfo userInfo);
}
