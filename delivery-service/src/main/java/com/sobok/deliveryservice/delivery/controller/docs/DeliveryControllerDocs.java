package com.sobok.deliveryservice.delivery.controller.docs;

import com.sobok.deliveryservice.common.dto.CommonResponse;
import com.sobok.deliveryservice.common.dto.TokenUserInfo;
import com.sobok.deliveryservice.delivery.dto.response.DeliveryAvailOrderResDto;
import com.sobok.deliveryservice.delivery.dto.response.DeliveryOrderResDto;
import com.sobok.deliveryservice.delivery.dto.response.RiderInfoResDto;
import com.sobok.deliveryservice.delivery.dto.response.RiderResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface DeliveryControllerDocs {

    @Operation(summary = "라이더 면허 번호 중복 확인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용 가능",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                {
                                    "success": true,
                                    "data": null,
                                    "message": "사용 가능한 면허번호 입니다.",
                                    "status": 200
                                }
                            """))),
            @ApiResponse(responseCode = "400", description = "중복된 면허번호",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                {
                                    "success": false,
                                    "data": null,
                                    "message": "이미 사용 중인 면허번호입니다.",
                                    "status": 400
                                }
                            """)))

    })
    ResponseEntity<?> checkPermission(@Parameter(description = "면허 번호", required = true)
                                      @RequestParam String permission);

    @Operation(summary = "배달 가능 주문 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "배달 가능 주문 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DeliveryAvailOrderResDto.class),
                            examples = @ExampleObject(value = """
                                {
                                    "success": true,
                                    "data": [
                                        {
                                            "shopId": 101,
                                            "shopName": "김밥천국",
                                            "shopRoadFull": "서울시 강남구 역삼동",
                                            "deliveryId": 201,
                                            "paymentId": 301,
                                            "orderId": "ORD123456",
                                            "orderState": "READY",
                                            "createdAt": "2025-08-04T10:00:00",
                                            "updatedAt": "2025-08-04T10:05:00",
                                            "roadFull": "서울시 송파구 잠실동",
                                            "addrDetail": "아파트 101동 1001호"
                                        }
                                    ],
                                    "message": "배달 가능한 주문 목록을 조회하였습니다.",
                                    "status": 200
                                }
                            """)))

    })
    ResponseEntity<?> getAllOrders(
            @Parameter(hidden = true) TokenUserInfo userInfo,
            @Parameter(description = "현재 위도", required = true) @RequestParam Double latitude,
            @Parameter(description = "현재 경도", required = true) @RequestParam Double longitude,
            @Parameter(description = "페이지 번호", required = true) @RequestParam Long pageNo,
            @Parameter(description = "페이지당 항목 수", required = true) @RequestParam Long numOfRows
    );

    @Operation(summary = "배달 중인 주문 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "배달 중인 주문 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DeliveryOrderResDto.class),
                            examples = @ExampleObject(value = """
                                {
                                    "success": true,
                                    "data": [
                                        {
                                            "orderId": "ORD78910",
                                            "paymentId": 401,
                                            "shopName": "맘스터치",
                                            "shopRoadFull": "서울시 강남구 논현동",
                                            "roadFull": "서울시 송파구 신천동",
                                            "addrDetail": "오피스텔 203호",
                                            "orderState": "DELIVERING",
                                            "completeTime": null
                                        }
                                    ],
                                    "message": "배달 중인 목록을 조회하였습니다.",
                                    "status": 200
                                }
                            """)))

    })
    ResponseEntity<?> getDeliveringOrders(
            @Parameter(hidden = true) TokenUserInfo userInfo,
            @Parameter(description = "페이지 번호", required = true) @RequestParam Long pageNo,
            @Parameter(description = "페이지당 항목 수", required = true) @RequestParam Long numOfRows
    );

    @Operation(summary = "라이더 배달 전체 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "전체 배달 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DeliveryOrderResDto.class),
                            examples = @ExampleObject(value = """
                                {
                                    "success": true,
                                    "data": [
                                        {
                                            "orderId": "ORD45678",
                                            "paymentId": 501,
                                            "shopName": "버거킹",
                                            "shopRoadFull": "서울시 강남구 삼성동",
                                            "roadFull": "서울시 송파구 잠실본동",
                                            "addrDetail": "빌라 101호",
                                            "orderState": "COMPLETED",
                                            "completeTime": "2025-08-03T18:30:00"
                                        }
                                    ],
                                    "message": "배달 전체 목록을 조회하였습니다.",
                                    "status": 200
                                }
                            """)))

    })
    ResponseEntity<?> getDeliveryOrders(
            @Parameter(hidden = true) TokenUserInfo userInfo,
            @Parameter(description = "페이지 번호", required = true) @RequestParam Long pageNo,
            @Parameter(description = "페이지당 항목 수", required = true) @RequestParam Long numOfRows
    );

    @Operation(summary = "전체 라이더 목록 조회 (관리자 전용)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "전체 라이더 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RiderInfoResDto.class),
                            examples = @ExampleObject(value = """
                                {
                                    "success": true,
                                    "data": [
                                        {
                                            "id": 1,
                                            "authId": 1001,
                                            "name": "김라이더",
                                            "phone": "01012345678",
                                            "permissionNumber": "123456789012",
                                            "active": "Y",
                                            "loginId": "kimrider"
                                        }
                                    ],
                                    "message": "전체 라이더 정보 조회 성공",
                                    "status": 200
                                }
                            """)))

    })
    ResponseEntity<?> getAllRiders(@Parameter(hidden = true) TokenUserInfo userInfo);

    @Operation(summary = "승인 대기중인 라이더 조회 (관리자 전용)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "비활성 라이더 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RiderResDto.class),
                            examples = @ExampleObject(value = """
                                {
                                    "success": true,
                                    "data": [
                                        {
                                            "id": 5,
                                            "name": "이대기",
                                            "phone": "01087654321",
                                            "permissionNumber": "987654321098",
                                            "active": "N"
                                        }
                                    ],
                                    "message": "비활성화된 라이더 정보 조회 성공.",
                                    "status": 200
                                }
                            """)))

    })
    ResponseEntity<?> getPendingRiders(@Parameter(hidden = true) TokenUserInfo userInfo);
}
