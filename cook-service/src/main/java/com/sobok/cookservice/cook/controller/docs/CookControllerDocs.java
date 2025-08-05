package com.sobok.cookservice.cook.controller.docs;

import com.sobok.cookservice.common.dto.CommonResponse;
import com.sobok.cookservice.cook.dto.request.CookCreateReqDto;
import com.sobok.cookservice.cook.dto.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface CookControllerDocs {

    @Operation(
            summary = "요리 등록",
            description = "새로운 요리를 등록합니다. 요리 이름, 카테고리, 썸네일, 기본 식재료 등을 포함하여 등록할 수 있습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "요리 등록 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "success": true,
                                      "data": { "cookId": 123 },
                                      "message": "요리 등록 성공",
                                      "status": 200
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "이미 존재하는 요리 이름, 썸네일 중복 또는 잘못된 입력",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "success": false,
                                      "data": null,
                                      "message": "이미 존재하는 요리 이름입니다.",
                                      "status": 400
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 식재료 ID",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "success": false,
                                      "data": null,
                                      "message": "해당 식재료가 존재하지 않습니다: id=99",
                                      "status": 404
                                    }
                                    """))),
            @ApiResponse(responseCode = "500", description = "사진 등록 실패 또는 서버 내부 오류",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "success": false,
                                      "data": null,
                                      "message": "사진 등록 실패",
                                      "status": 500
                                    }
                                    """))),
    })
    ResponseEntity<?> registerCook(
            @Parameter(description = "요리 등록 요청 DTO", required = true)
            @RequestBody CookCreateReqDto dto
    );

    @Operation(
            summary = "요리 전체 조회 (페이징)",
            description = "페이지 번호와 페이지당 항목 수를 지정하여 페이징 처리된 요리 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "페이징된 모든 요리 목록 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "success": true,
                                      "data": [
                                        {
                                          "cookId": 1,
                                          "name": "김치찌개",
                                          "category": "한식",
                                          "thumbnailUrl": "https://example.com/images/kimchi.jpg"
                                        },
                                        {
                                          "cookId": 2,
                                          "name": "불고기",
                                          "category": "한식",
                                          "thumbnailUrl": "https://example.com/images/bulgogi.jpg"
                                        }
                                      ],
                                      "message": "요리 목록 조회 성공",
                                      "status": 200
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "잘못된 페이지 번호 또는 항목 수",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "success": false,
                                      "data": null,
                                      "message": "잘못된 페이지 번호입니다.",
                                      "status": 400
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "잘못된 카테고리 입력",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "success": false,
                                      "data": null,
                                      "message": "잘못된 카테고리 입력입니다.",
                                      "status": 400
                                    }
                                    """))),
    })
    ResponseEntity<?> getCook(
            @Parameter(description = "페이지 번호", required = true) @RequestParam Long pageNo,
            @Parameter(description = "페이지당 항목 수", required = true) @RequestParam Long numOfRows
    );

    @Operation(
            summary = "키워드로 요리 검색 (페이징)",
            description = "검색 키워드를 기반으로 요리를 검색하며, 페이지 번호와 페이지당 항목 수로 결과를 페이징 처리합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "키워드로 요리 목록 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "success": true,
                                      "data": [
                                        {
                                          "cookId": 3,
                                          "name": "된장찌개",
                                          "category": "한식",
                                          "thumbnailUrl": "https://example.com/images/doenjang.jpg"
                                        }
                                      ],
                                      "message": "검색 결과 조회 성공",
                                      "status": 200
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "잘못된 페이지 번호, 항목 수 또는 빈 키워드",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "success": false,
                                      "data": null,
                                      "message": "키워드가 비어있거나 유효하지 않습니다.",
                                      "status": 400
                                    }
                                    """))),
    })
    ResponseEntity<?> getCook(
            @Parameter(description = "검색 키워드", required = true) @RequestParam String keyword,
            @Parameter(description = "페이지 번호", required = true) @RequestParam Long pageNo,
            @Parameter(description = "페이지당 항목 수", required = true) @RequestParam Long numOfRows
    );

    @Operation(
            summary = "카테고리로 요리 검색 (페이징)",
            description = "카테고리명을 기준으로 요리를 검색하며, 페이지 번호와 페이지당 항목 수로 결과를 페이징 처리합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "카테고리로 요리 목록 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "success": true,
                                      "data": [
                                        {
                                          "cookId": 4,
                                          "name": "파스타",
                                          "category": "양식",
                                          "thumbnailUrl": "https://example.com/images/pasta.jpg"
                                        }
                                      ],
                                      "message": "카테고리 검색 결과 조회 성공",
                                      "status": 200
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "잘못된 카테고리 입력",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "success": false,
                                      "data": null,
                                      "message": "잘못된 카테고리 입력입니다.",
                                      "status": 400
                                    }
                                    """))),
    })
    ResponseEntity<?> getCookCategory(
            @Parameter(description = "카테고리 이름", required = true) @RequestParam String category,
            @Parameter(description = "페이지 번호", required = true) @RequestParam Long pageNo,
            @Parameter(description = "페이지당 항목 수", required = true) @RequestParam Long numOfRows
    );

    @Operation(
            summary = "단일 요리 조회",
            description = "특정 요리 ID에 해당하는 상세 정보를 조회합니다. 이름, 카테고리, 설명, 알레르기 정보 등이 포함됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "요리 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "success": true,
                                      "data": {
                                        "cookId": 1,
                                        "name": "김치찌개",
                                        "category": "한식",
                                        "description": "매콤한 김치와 돼지고기를 넣은 찌개",
                                        "thumbnailUrl": "https://example.com/images/kimchi.jpg",
                                        "allergens": ["돼지고기", "대두"]
                                      },
                                      "message": "요리 상세 조회 성공",
                                      "status": 200
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "해당 요리가 존재하지 않음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "success": false,
                                      "data": null,
                                      "message": "일치하는 요리가 존재하지 않습니다.",
                                      "status": 404
                                    }
                                    """))),
    })
    ResponseEntity<?> getCookById(
            @Parameter(description = "조회할 요리 ID", required = true) @PathVariable("id") Long cookId
    );


    @Operation(
            summary = "한달 주문량 기준 요리 페이지 조회",
            description = "한 달간 주문량을 기준으로 인기 있는 요리 목록을 페이지 단위로 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인기 요리 페이징 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PagedResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "success": true,
                                      "data": {
                                        "content": [
                                          {
                                            "cookId": 1,
                                            "name": "김치찌개",
                                            "orderCount": 150,
                                            "thumbnailUrl": "https://example.com/images/kimchi.jpg"
                                          },
                                          {
                                            "cookId": 2,
                                            "name": "불고기",
                                            "orderCount": 120,
                                            "thumbnailUrl": "https://example.com/images/bulgogi.jpg"
                                          }
                                        ],
                                        "page": 0,
                                        "size": 10,
                                        "totalPages": 5,
                                        "totalElements": 50,
                                        "first": true,
                                        "last": false
                                      },
                                      "message": "인기 요리 목록 조회 성공",
                                      "status": 200
                                    }
                                    """))),
            @ApiResponse(responseCode = "204", description = "주문 데이터가 없음",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "요리 정보를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "success": false,
                                      "data": null,
                                      "message": "요리 정보가 없습니다. id=1",
                                      "status": 404
                                    }
                                    """))),
            @ApiResponse(responseCode = "500", description = "payment-service 통신 실패 또는 서버 오류",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "success": false,
                                      "data": null,
                                      "message": "payment-service 통신 실패",
                                      "status": 500
                                    }
                                    """)))
    })
    @GetMapping("/popular")
    PagedResponse<PopularCookResDto> getPopularCooks(
            @Parameter(description = "페이지 번호 (0부터 시작)", required = true) @RequestParam int page,
            @Parameter(description = "페이지 크기", required = true) @RequestParam int size
    );

    @Operation(
            summary = "한달 주문량 기준 요리 목록 전체 조회",
            description = "한 달간 주문량 기준으로 인기 있는 요리 전체 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인기 요리 전체 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "success": true,
                                      "data": [
                                        {
                                          "cookId": 1,
                                          "name": "김치찌개",
                                          "orderCount": 150,
                                          "thumbnailUrl": "https://example.com/images/kimchi.jpg"
                                        },
                                        {
                                          "cookId": 2,
                                          "name": "불고기",
                                          "orderCount": 120,
                                          "thumbnailUrl": "https://example.com/images/bulgogi.jpg"
                                        },
                                        {
                                          "cookId": 3,
                                          "name": "된장찌개",
                                          "orderCount": 110,
                                          "thumbnailUrl": "https://example.com/images/doenjang.jpg"
                                        }
                                      ],
                                      "message": "인기 요리 조회 성공",
                                      "status": 200
                                    }
                                    """))),
            @ApiResponse(responseCode = "500", description = "payment-service 오류",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "success": false,
                                      "data": null,
                                      "message": "payment-service 오류",
                                      "status": 500
                                    }
                                    """))),
    })
    ResponseEntity<?> getMonthlyHotCooks(
            @Parameter(description = "페이지 번호", required = true) @RequestParam int pageNo,
            @Parameter(description = "페이지당 항목 수", required = true) @RequestParam int numOfRows
    );
}
