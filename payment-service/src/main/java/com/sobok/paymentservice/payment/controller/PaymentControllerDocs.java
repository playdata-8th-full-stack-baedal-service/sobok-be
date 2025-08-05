package com.sobok.paymentservice.payment.controller;

import com.sobok.paymentservice.common.dto.CommonResponse;
import com.sobok.paymentservice.common.dto.TokenUserInfo;
import com.sobok.paymentservice.common.enums.DeliveryState;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "주문 관리 (PaymentController)", description = "주문 관련 기능 제공")
@RequestMapping("/payment")
public interface PaymentControllerDocs {

    @Operation(
            summary = "주문 사전 정보 등록",
            description = """
                    사용자의 장바구니 정보를 기반으로 주문 사전 정보를 등록합니다.  
                    실제 결제 API 요청 전, 사용자 주문 데이터를 서버에 임시 저장하기 위해 호출됩니다.
                    
                    ### 요청 정보
                    - 인증 불필요
                    - 아래 항목들을 포함한 JSON 요청 본문
                    
                    ### 요청 예시
                    ```json
                    {
                      "orderId": "ORDER_20250805_0001",
                      "totalPrice": 39000,
                      "riderRequest": "문 앞에 놓아주세요.",
                      "userAddressId": 7,
                      "cartCookIdList": [11, 12, 13]
                    }
                    ```
                    
                    ### 응답 정보
                    - 생성된 `paymentId` 반환
                    
                    ### 예외
                    - 누락된 필드 또는 잘못된 데이터 형식
                    - 존재하지 않는 사용자 주소 ID
                    - 장바구니 ID가 유효하지 않음
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
                    description = "요청 형식 오류 또는 검증 실패",
                    content = @Content(
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "status": 400,
                                              "message": "총 금액은 필수 항목입니다.",
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
                    
                    ### 요청 정보
                    - 인증 필요 (Bearer Token)
                    - 요청 본문에 요리 ID, 추가 재료 리스트, 수량 포함
                    
                    ### 요청 예시
                    ```json
                    {
                      "cookId": 5,
                      "additionalIngredients": [
                        { "ingreId": 3, "unitQuantity": 2 },
                        { "ingreId": 4, "unitQuantity": 1 }
                      ],
                      "count": 2
                    }
                    ```
                    
                    ### 응답 정보
                    - 생성된 `cartCookId` 반환
                    
                    ### 예외
                    - 존재하지 않는 요리 ID 또는 재료 ID
                    - 수량이 1 미만
                    - 인증 실패
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
            description = """
                    로그인한 사용자의 장바구니에 담긴 요리 목록을 조회합니다.
                    
                    ### 요청 정보
                    - 인증 필요 (Bearer Token)
                    - 별도 요청 파라미터 없음
                    
                    ### 응답 정보
                    - 사용자 ID와 요리별 장바구니 항목 리스트 (`items`)를 반환합니다.
                    - 각 요리는 기본 식재료(`baseIngredients`)와 추가 식재료(`additionalIngredients`) 정보를 포함합니다.
                    
                    ### 예외
                    - 장바구니가 비어 있는 경우
                    - 인증 실패
                    """,
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
                                                      },
                                                      {
                                                        "ingredientId": 22,
                                                        "ingreName": "소금",
                                                        "unitQuantity": 4,
                                                        "unit": 5,
                                                        "price": 20,
                                                        "origin": "국내산"
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
                    description = "인증 실패 (토큰 없음 또는 만료)",
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
    @GetMapping("/get-cart")
    ResponseEntity<?> getCart(
            @Parameter(hidden = true) @AuthenticationPrincipal TokenUserInfo userInfo
    );

    @Operation(
            summary = "결제 실패 또는 취소 처리",
            description = """
                    결제 실패 또는 사용자의 결제 취소 시, 사전 등록된 주문 정보를 초기화합니다.
                    
                    ### 요청 정보
                    - 인증 불필요
                    - 쿼리 파라미터로 `orderId`를 전달해야 합니다.
                    
                    ### 요청 예시
                    ```
                    DELETE /api/payment/fail-payment?orderId=ORDER_20250805_0001
                    ```
                    
                    ### 응답 정보
                    - 성공 메시지와 함께 상태 코드 200 반환
                    
                    ### 예외
                    - 존재하지 않는 주문 ID
                    - 이미 취소되었거나 완료된 주문
                    """
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
            description = """
                    지정한 주문 ID에 해당하는 결제 정보를 삭제합니다.  
                    주로 테스트 목적이거나, 결제가 완료되지 않은 주문 데이터를 제거할 때 사용됩니다.
                    
                    ### 요청 정보
                    - 인증 불필요
                    - `orderId`는 쿼리 파라미터로 전달
                    
                    ### 요청 예시
                    ```
                    DELETE /delete-payment?orderId=ORDER_20250805_0001
                    ```
                    
                    ### 응답 정보
                    - 응답 본문 없음 (`204 No Content` 가능)
                    
                    ### 예외
                    - 존재하지 않는 주문 ID
                    """
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
                    
                    ### 요청 예시
                    ```
                    GET /get-myPayment?pageNo=1&numOfRows=10
                    ```
                    
                    ### 응답 정보
                    - 주문 내역 리스트 반환
                    - 주문이 없으면 빈 리스트 또는 HTTP 204 상태 반환
                    
                    ### 예외
                    - 인증 실패
                    - 파라미터 유효성 오류
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
                                                  "orderId": "ORDER_20250805_0001",
                                                  "totalPrice": 39000,
                                                  "orderState": "COMPLETED",
                                                  "createdAt": "2025-08-01T15:23:01",
                                                  "cook": [
                                                    {
                                                      "cookId": 1,
                                                      "cookName": "김치찌개",
                                                      "thumbnail": "kimchistew001.png"
                                                    },
                                                    {
                                                      "cookId": 2,
                                                      "cookName": "비빔밥",
                                                      "thumbnail": "bibimbap001.png"
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
            description = """
                    인증된 사용자의 특정 주문 ID(paymentId)에 대한 상세 주문 정보를 조회합니다.
                    
                    ### 요청 정보
                    - 인증 필요 (Bearer Token)
                    - Path Variable로 조회할 주문 ID 전달
                    
                    ### 요청 예시
                    ```
                    GET /detail/101
                    ```
                    
                    ### 응답 정보
                    - 주문 상세 정보 반환 (결제 정보, 주문 상태, 배달 요청 사항, 주소, 주문 항목 등)
                    
                    ### 예외
                    - 인증 실패
                    - 주문 ID 존재하지 않음
                    - 권한 없음 (다른 사용자의 주문 조회 시도)
                    """
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
                                              "status": 200,
                                              "message": "주문 상세 내역이 조회되었습니다.",
                                              "data": {
                                                "paymentId": 101,
                                                "orderId": "ORDER_20250805_0001",
                                                "totalPrice": 39000,
                                                "orderState": "DELIVERED",
                                                "createdAt": "2025-08-01T15:23:01",
                                                "payMethod": "카드결제",
                                                "riderRequest": "문 앞에 놓아주세요.",
                                                "items": [
                                                  {
                                                    "id": 24,
                                                    "cookId": 17,
                                                    "cookName": "오이샐러드",
                                                    "thumbnail": "https://example.com/images/food1.jpg",
                                                    "active": "Y",
                                                    "quantity": 1,
                                                    "baseIngredients": [...],
                                                    "additionalIngredients": [...],
                                                    "paymentId": 101
                                                  }
                                                ],
                                                "roadFull": "서울시 강남구 테헤란로 123",
                                                "addrDetail": "101동 202호",
                                                "shopName": "맛집 A",
                                                "shopAddress": "서울시 강남구 역삼동 456-7",
                                                "completeTime": "2025-08-01T16:15:00"
                                              }
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
            description = """
                    인증된 사용자가 특정 주문 ID에 대해 주문 상태를 변경합니다.
                    
                    ### 요청 정보
                    - 인증 필요 (Bearer Token)
                    - 변경할 주문 ID를 쿼리 파라미터로 전달
                    
                    ### 요청 예시
                    ```
                    PATCH /change-orderState?id=101
                    ```
                    
                    ### 응답 정보
                    - 변경된 주문 ID 반환
                    
                    ### 예외
                    - 인증 실패
                    - 주문 ID 존재하지 않음
                    - 권한 없음 (다른 사용자의 주문 상태 변경 시도)
                    """
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
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음",
                    content = @Content(
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
            description = """
                    인증된 사용자가 특정 주문에 대해 배달을 승인합니다.
                    
                    ### 요청 정보
                    - 인증 필요 (Bearer Token)
                    - 승인할 주문 ID를 쿼리 파라미터로 전달
                    
                    ### 요청 예시
                    ```
                    PATCH /accept-delivery?id=101
                    ```
                    
                    ### 응답 정보
                    - 승인된 주문 ID 반환
                    
                    ### 예외
                    - 인증 실패
                    - 주문 ID 존재하지 않음
                    - 권한 없음 (다른 사용자의 주문 승인 시도)
                    - 배달 승인 처리 실패
                    """
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
                    description = "잘못된 요청 또는 배달 승인 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "status": 400,
                                              "message": "배달 승인 처리 중 오류가 발생했습니다.",
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
    @PatchMapping("/accept-delivery")
    ResponseEntity<?> acceptDelivery(
            @Parameter(hidden = true) @AuthenticationPrincipal TokenUserInfo userInfo,
            @Parameter(description = "주문 ID", example = "101", required = true) @RequestParam Long id
    );

    @Operation(
            summary = "배달 완료 처리",
            description = """
                    인증된 사용자가 특정 주문에 대해 배달 완료 처리를 합니다.
                    
                    ### 요청 정보
                    - 인증 필요 (Bearer Token)
                    - 완료 처리할 주문 ID를 쿼리 파라미터로 전달
                    
                    ### 요청 예시
                    ```
                    PATCH /complete-delivery?id=101
                    ```
                    
                    ### 응답 정보
                    - 완료 처리된 주문 ID 반환
                    
                    ### 예외
                    - 인증 실패
                    - 주문 ID 존재하지 않음
                    - 권한 없음 (다른 사용자의 주문 배달 완료 처리 시도)
                    - 배달 완료 처리 실패
                    """
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
            description = """
                    관리자 권한으로 전체 주문 내역을 페이지 단위로 조회합니다.
                    
                    ### 요청 정보
                    - 인증 불필요(필요하다면 인증 추가 가능)
                    - 페이지 번호(`page`)와 페이지 크기(`size`)는 쿼리 파라미터로 전달 (기본값 각각 0, 10)
                    
                    ### 요청 예시
                    ```
                    GET /all?page=0&size=10
                    ```
                    
                    ### 응답 정보
                    - `AdminPaymentBasicResDto` 리스트 반환 (주문 ID, 주문 번호, 생성일시 포함)
                    
                    ### 예외
                    - 데이터가 없을 경우 빈 리스트 또는 204 상태 반환 가능
                    """
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
                                                  "orderId": "ORDER_20250805_0001",
                                                  "createdAt": "2025-08-01T15:23:01"
                                                },
                                                {
                                                  "paymentId": 102,
                                                  "orderId": "ORDER_20250805_0002",
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
            description = """
                    주문 ID에 해당하는 상세 주문 정보를 관리자 권한으로 조회합니다.
                    
                    ### 요청 정보
                    - 인증 불필요 (필요시 인증 추가)
                    - Path Variable로 주문 ID 전달
                    
                    ### 요청 예시
                    ```
                    GET /all/101
                    ```
                    
                    ### 응답 정보
                    - 주문 상세 정보 반환 (주문, 유저, 라이더, 가게, 요리 정보 포함)
                    
                    ### 예외
                    - 주문 ID 미존재
                    """
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
                                                "orderId": "ORDER_20250805_0001",
                                                "totalPrice": 39000,
                                                "payMethod": "카드결제",
                                                "orderState": "DELIVERED",
                                                "createdAt": "2025-08-01T15:23:01",
                                                "completeTime": "2025-08-01T16:15:00",
                                                "loginId": "user123",
                                                "nickname": "홍길동",
                                                "roadFull": "서울시 강남구 테헤란로 123",
                                                "address": "101동 202호",
                                                "phone": "010-1234-5678",
                                                "riderName": "김배달",
                                                "riderPhone": "010-9876-5432",
                                                "shopName": "맛집 A",
                                                "shopAddress": "서울시 강남구 역삼동 456-7",
                                                "ownerName": "사장님",
                                                "shopPhone": "02-123-4567",
                                                "cooks": [
                                                  {
                                                    "cookName": "오이샐러드",
                                                    "baseIngredients": ["계란", "양파"],
                                                    "additionalIngredients": ["비엔나소세지"]
                                                  },
                                                  {
                                                    "cookName": "토마토 파스타",
                                                    "baseIngredients": ["토마토", "스파게티 면", "소금"],
                                                    "additionalIngredients": []
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
