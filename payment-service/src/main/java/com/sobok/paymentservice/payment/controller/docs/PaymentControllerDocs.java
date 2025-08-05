package com.sobok.paymentservice.payment.controller.docs;

import com.sobok.paymentservice.common.dto.CommonResponse;
import com.sobok.paymentservice.common.dto.TokenUserInfo;
import com.sobok.paymentservice.payment.dto.cart.CartAddCookReqDto;
import com.sobok.paymentservice.payment.dto.payment.AdminPaymentBasicResDto;
import com.sobok.paymentservice.payment.dto.payment.AdminPaymentResponseDto;
import com.sobok.paymentservice.payment.dto.payment.PaymentRegisterReqDto;
import com.sobok.paymentservice.payment.dto.response.GetPaymentResDto;
import com.sobok.paymentservice.payment.dto.response.PaymentDetailResDto;
import com.sobok.paymentservice.payment.dto.response.PaymentResDto;
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

@Tag(name = "주문 관리 (PaymentController)", description = "주문 관련 기능 제공")
@RequestMapping("/payment")
public interface PaymentControllerDocs {

    @Operation(
            summary = "주문 사전 정보 등록",
            description = """
                    사용자의 장바구니 정보를 기반으로 주문 사전 정보를 등록합니다.  
                    실제 결제 API 요청 전, 사용자 주문 데이터를 서버에 임시 저장하기 위해 호출됩니다.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "주문 사전 정보 등록 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "등록 성공 예시",
                                    value = """
                                            {
                                              "success": true,
                                              "status": 200,
                                              "message": "주문 사전 정보가 정상적으로 저장되었습니다.",
                                              "data": 101
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (장바구니 요리 정보 없음)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "status": 400,
                                              "message": "해당하는 장바구니 요리가 없습니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 주소 ID 또는 장바구니 항목",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "status": 404,
                                              "message": "해당 주소 정보를 찾을 수 없습니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/register")
    ResponseEntity<?> registerPayment(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "주문 사전 정보 등록 요청 본문",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PaymentRegisterReqDto.class))
            )
            @RequestBody PaymentRegisterReqDto reqDto
    );

    @Operation(
            summary = "장바구니 요리 추가",
            description = """
                    사용자가 선택한 요리를 장바구니에 추가합니다.  
                    추가 재료 정보와 수량도 함께 전달할 수 있습니다.
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "장바구니 추가 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "추가 성공 예시",
                                    value = """
                                            {
                                              "success": true,
                                              "status": 200,
                                              "message": "장바구니에 성공적으로 저장되었습니다.",
                                              "data": 27
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청 형식 오류 또는 유효성 검증 실패",
                    content = @Content(
                            mediaType = "application/json",
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
                    description = "존재하지 않는 요리 또는 재료 ID",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "status": 404,
                                              "message": "해당 요리를 찾을 수 없습니다.",
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
                                              "message": "로그인이 필요합니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/add-cart")
    ResponseEntity<?> addCartCook(
            @Parameter(hidden = true) @AuthenticationPrincipal TokenUserInfo userInfo,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "장바구니 추가 요청 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CartAddCookReqDto.class))
            )
            @RequestBody CartAddCookReqDto reqDto
    );

    @Operation(
            summary = "장바구니 조회",
            description = "로그인한 사용자의 장바구니에 담긴 요리 목록을 조회합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "장바구니 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PaymentResDto.class),
                            examples = @ExampleObject(
                                    name = "장바구니 조회 예시",
                                    value = """
                                            {
                                              "success": true,
                                              "data": {
                                                "userId": 1,
                                                "items": [
                                                  {
                                                    "id": 25,
                                                    "cookId": 14,
                                                    "cookName": "토마토 파스타",
                                                    "thumbnail": "https://...jpg",
                                                    "active": "Y",
                                                    "quantity": 1,
                                                    "baseIngredients": [
                                                      {
                                                        "ingredientId": 25,
                                                        "ingreName": "토마토",
                                                        "unitQuantity": 40,
                                                        "unit": 5,
                                                        "price": 4,
                                                        "origin": "국내산"
                                                      },
                                                      {
                                                        "ingredientId": 26,
                                                        "ingreName": "스파게티 면",
                                                        "unitQuantity": 2,
                                                        "unit": 50,
                                                        "price": 10,
                                                        "origin": "미국"
                                                      }
                                                    ],
                                                    "additionalIngredients": [
                                                      {
                                                        "ingredientId": 8,
                                                        "ingreName": "비엔나소세지",
                                                        "unitQuantity": 1,
                                                        "unit": 100,
                                                        "price": 130,
                                                        "origin": "국산"
                                                      }
                                                    ],
                                                    "paymentId": null
                                                  }
                                                ]
                                              },
                                              "message": "장바구니 조회 성공",
                                              "status": 200
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "장바구니가 비어 있는 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "status": 404,
                                              "message": "장바구니가 존재하지 않습니다.",
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
                                              "message": "로그인이 필요합니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/get-cart")
    ResponseEntity<?> getCart(
            @Parameter(hidden = true) @AuthenticationPrincipal TokenUserInfo userInfo
    );

    @Operation(
            summary = "결제 실패 또는 취소 처리",
            description = " 결제 실패 또는 사용자의 결제 취소 시, 사전 등록된 주문 정보를 초기화합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "결제 취소 처리 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "결제 취소 성공 예시",
                                    value = """
                                            {
                                              "success": true,
                                              "status": 200,
                                              "message": "성공적으로 결제가 취소되었습니다.",
                                              "data": "결제 정보 초기화 완료"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 주문 ID",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "status": 404,
                                              "message": "해당 주문 정보를 찾을 수 없습니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "이미 취소되었거나 완료된 주문",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "status": 400,
                                              "message": "이미 처리된 주문입니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @DeleteMapping("/fail-payment")
    ResponseEntity<?> cancelPayment(
            @Parameter(description = "결제 실패 또는 취소 처리할 주문 ID", example = "ORDER_20250805_0001", required = true)
            @RequestParam String orderId
    );

    @Operation(
            summary = "결제 정보 삭제",
            description = "지정한 주문 ID에 해당하는 결제 정보를 삭제합니다. 결제가 완료되지 않은 주문 데이터를 제거할 때 사용됩니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "결제 정보 삭제 성공 - 응답 본문 없음"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 주문 ID",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "status": 404,
                                              "message": "해당 주문 ID에 대한 결제 정보를 찾을 수 없습니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (결제 정보 없음 또는 카트 정보 없음)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "status": 400,
                                              "message": "해당하는 결제 내역이 존재하지 않습니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @DeleteMapping("/delete-payment")
    void deletePayment(@RequestParam String orderId);

    @Operation(
            summary = "사용자 주문 내역 조회",
            description = """
                    인증된 사용자의 주문 내역을 페이지 단위로 조회합니다.
                    ### 요청 정보
                    - 인증 필요 (Bearer Token)
                    - 페이지 번호(pageNo)와 페이지당 항목 수(numOfRows)를 쿼리 파라미터로 전달
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "주문 내역 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GetPaymentResDto.class),
                            examples = @ExampleObject(
                                    name = "주문 내역 조회 성공 예시",
                                    value = """
                                            {
                                              "success": true,
                                              "status": 200,
                                              "message": "사용자의 주문 내역이 조회되었습니다.",
                                              "data": [
                                                {
                                                  "paymentId": 101,
                                                  "orderId": "20250801x6ATP328",
                                                  "totalPrice": 39000,
                                                  "orderState": "DELIVERY_COMPLETE",
                                                  "createdAt": "2025-08-01T15:23:01",
                                                  "cook": [
                                                    {
                                                      "cookId": 1,
                                                      "cookName": "김치찌개",
                                                      "thumbnail": "https://...jpg"
                                                    },
                                                    {
                                                      "cookId": 2,
                                                      "cookName": "비빔밥",
                                                      "thumbnail": "https://...jpg"
                                                    }
                                                  ]
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "주문 내역 없음 (컨텐츠 없음)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "주문 내역 없음 예시",
                                    value = """
                                            {
                                              "success": true,
                                              "status": 204,
                                              "message": "조회된 주문 내역이 없습니다.",
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
                                              "message": "로그인이 필요합니다.",
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
                                    name = "파라미터 오류 예시",
                                    value = """
                                            {
                                              "success": false,
                                              "status": 400,
                                              "message": "pageNo 와 numOfRows 는 필수 파라미터입니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (요리 정보 조회 실패)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "status": 400,
                                              "message": "요리 정보를 불러오지 못했습니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "접근 권한 없음 (사용자 검증 실패)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "status": 403,
                                              "message": "접근 불가",
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "내부 서버 오류 (요리 정보 누락)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "status": 500,
                                              "message": "요리 정보가 없습니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/get-myPayment")
    ResponseEntity<?> getPayment(
            @Parameter(hidden = true) @AuthenticationPrincipal TokenUserInfo userInfo,
            @Parameter(description = "조회할 페이지 번호", example = "1", required = true) @RequestParam Long pageNo,
            @Parameter(description = "페이지당 항목 수", example = "10", required = true) @RequestParam Long numOfRows
    );

    @Operation(
            summary = "주문 상세 내역 조회",
            description = "인증된 사용자의 특정 주문 ID(paymentId)에 대한 상세 주문 정보를 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "주문 상세 내역 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PaymentDetailResDto.class),
                            examples = @ExampleObject(
                                    name = "주문 상세 조회 성공 예시",
                                    value = """
                                            {
                                                "success": true,
                                                "data": {
                                                    "paymentId": 8,
                                                    "orderId": "20250729x1c5uydN",
                                                    "totalPrice": 16100,
                                                    "orderState": "DELIVERY_COMPLETE",
                                                    "createdAt": "2011-09-21T10:32:02.216484",
                                                    "payMethod": "간편결제",
                                                    "riderRequest": "X",
                                                    "items": [
                                                        {
                                                            "id": 13,
                                                            "cookId": 3,
                                                            "cookName": "프렌치 토스트",
                                                            "thumbnail": "https://...png",
                                                            "active": "Y",
                                                            "quantity": 1,
                                                            "baseIngredients": [
                                                                {
                                                                    "ingredientId": 11,
                                                                    "ingreName": "계란",
                                                                    "unitQuantity": 8,
                                                                    "unit": 5,
                                                                    "price": 100,
                                                                    "origin": "국내산"
                                                                },
                                                                {
                                                                    "ingredientId": 12,
                                                                    "ingreName": "식빵",
                                                                    "unitQuantity": 1,
                                                                    "unit": 10,
                                                                    "price": 1000,
                                                                    "origin": "호주"
                                                                },
                                                            ],
                                                            "additionalIngredients": [],
                                                            "paymentId": 8
                                                        }
                                                    ],
                                                    "roadFull": "서울시 강남구 테헤란로",
                                                    "addrDetail": "13층 1301호",
                                                    "shopName": "당근가게",
                                                    "shopAddress": "서초동 1660-27",
                                                    "completeTime": "2025-08-04T10:41:46.843057"
                                                },
                                                "message": "주문 상세 내역이 조회되었습니다.",
                                                "status": 200
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (주문 정보 없음, 카트 정보 없음, 배달 정보 조회 실패, 역할 접근 권한 없음)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "status": 400,
                                              "message": "주문 정보가 존재하지 않습니다.",
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
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음 (다른 사용자의 주문 조회 시도)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "권한 없음 예시",
                                    value = """
                                            {
                                              "success": false,
                                              "status": 403,
                                              "message": "해당 주문에 접근 권한이 없습니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 주문 ID",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    name = "주문 미존재 예시",
                                    value = """
                                            {
                                              "success": false,
                                              "status": 404,
                                              "message": "해당 주문 정보를 찾을 수 없습니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/detail/{id}")
    ResponseEntity<?> getPaymentDetail(
            @Parameter(hidden = true) @AuthenticationPrincipal TokenUserInfo userInfo,
            @Parameter(description = "조회할 주문 ID", example = "101", required = true) @PathVariable("id") Long paymentId
    );

    @Operation(
            summary = "주문 상태 변경",
            description = "인증된 사용자가 특정 주문 ID에 대해 주문 상태를 변경합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "주문 상태 변경 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": true,
                                              "status": 200,
                                              "message": "주문 상태가 변경되었습니다.",
                                              "data": 101
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (배달 정보 조회 실패, 상태 변경 불가)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "status": 400,
                                              "message": "현재 상태에서는 상태 변경이 허용되지 않습니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "접근 권한 없음 (지원하지 않는 역할)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "status": 403,
                                              "message": "지원하지 않는 역할입니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "주문 정보를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "status": 404,
                                              "message": "해당 주문 정보를 찾을 수 없습니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @PatchMapping("/change-orderState")
    ResponseEntity<?> changeOrderState(
            @Parameter(hidden = true) @AuthenticationPrincipal TokenUserInfo userInfo,
            @Parameter(description = "주문 ID", example = "101", required = true) @RequestParam Long id
    );

    @Operation(
            summary = "배달 승인 처리",
            description = "인증된 사용자가 특정 주문에 대해 배달을 승인합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "배달 승인 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": true,
                                              "status": 200,
                                              "message": "배달이 승인되었습니다.",
                                              "data": 101
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (상태 변경 불가)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "status": 400,
                                              "message": "READY_FOR_DELIVERY 상태에서만 ASSIGN 작업이 가능합니다.",
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
                                              "message": "로그인이 필요합니다.",
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
                                    value = """
                                            {
                                              "success": false,
                                              "status": 403,
                                              "message": "해당 주문에 대한 권한이 없습니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "주문 정보를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "status": 404,
                                              "message": "해당 주문 정보를 찾을 수 없습니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @PatchMapping("/accept-delivery")
    ResponseEntity<?> acceptDelivery(
            @Parameter(hidden = true) @AuthenticationPrincipal TokenUserInfo userInfo,
            @Parameter(description = "주문 ID", example = "101", required = true) @RequestParam Long id
    );

    @Operation(
            summary = "배달 완료 처리",
            description = "인증된 사용자가 특정 주문에 대해 배달 완료 처리를 합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "배달 완료 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": true,
                                              "status": 200,
                                              "message": "배달이 완료되었습니다.",
                                              "data": 101
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 또는 배달 완료 처리 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "status": 400,
                                              "message": "배달 완료 처리 중 오류가 발생했습니다.",
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
                                              "message": "로그인이 필요합니다.",
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
                                    value = """
                                            {
                                              "success": false,
                                              "status": 403,
                                              "message": "해당 주문에 대한 권한이 없습니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 주문 ID",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "status": 404,
                                              "message": "해당 주문 정보를 찾을 수 없습니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @PatchMapping("/complete-delivery")
    ResponseEntity<?> completeDelivery(
            @Parameter(hidden = true) @AuthenticationPrincipal TokenUserInfo userInfo,
            @Parameter(description = "주문 ID", example = "101", required = true) @RequestParam Long id
    );

    @Operation(
            summary = "전체 주문 목록 조회 (관리자용)",
            description = "관리자 권한으로 전체 주문 내역을 페이지 단위로 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "주문 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AdminPaymentBasicResDto.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": true,
                                              "status": 200,
                                              "message": "전체 주문 조회 성공",
                                              "data": [
                                                {
                                                  "paymentId": 101,
                                                  "orderId": "20250801x6ATP328",
                                                  "createdAt": "2025-08-01T15:23:01"
                                                },
                                                {
                                                  "paymentId": 102,
                                                  "orderId": "20250801x6ATP328",
                                                  "createdAt": "2025-08-02T10:15:00"
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "조회된 주문 내역이 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": true,
                                              "status": 204,
                                              "message": "조회된 주문 내역이 없습니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/all")
    ResponseEntity<?> getAllOrders(
            @Parameter(description = "페이지 번호 (기본값 0)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기 (기본값 10)", example = "10") @RequestParam(defaultValue = "10") int size
    );

    @Operation(
            summary = "관리자 주문 상세 조회",
            description = "주문 ID에 해당하는 상세 주문 정보를 관리자 권한으로 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "주문 상세 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AdminPaymentResponseDto.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": true,
                                              "status": 200,
                                              "message": "주문 상세 조회 성공",
                                              "data": {
                                                "orderId": "20250801x6ATP328",
                                                "totalPrice": 39000,
                                                "payMethod": "간편결제",
                                                "orderState": "DELIVERY_COMPLETE",
                                                "createdAt": "2025-08-01T15:23:01",
                                                "completeTime": "2025-08-01T16:15:00",
                                                "loginId": "user123",
                                                "nickname": "홍길동",
                                                "roadFull": "서울시 강남구 테헤란로 123",
                                                "addrDetail": "101동 202호",
                                                "phone": "01012345678",
                                                "riderName": "김배달",
                                                "riderPhone": "01098765432",
                                                "shopName": "맛집 A",
                                                "shopAddress": "서울시 강남구 역삼동 456-7",
                                                "ownerName": "사장님",
                                                "shopPhone": "01012345678",
                                                "cooks": [
                                                  {
                                                    "cookName": "오이샐러드",
                                                    "baseIngredients": ["계란", "양파"],
                                                    "additionalIngredients": ["비엔나소세지"]
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
                    description = "존재하지 않는 주문 ID",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "status": 404,
                                              "message": "해당 주문 정보를 찾을 수 없습니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/all/{id}")
    ResponseEntity<?> getOrderDetail(
            @Parameter(description = "조회할 주문 ID", example = "101", required = true) @PathVariable("id") Long paymentId
    );
}
