package com.sobok.cookservice.cook.controller.docs;

import com.sobok.cookservice.common.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface CookDisplayControllerDocs {

    @Operation(summary = "조건에 맞는 요리 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "요리 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "data": [
                        {
                          "cookId": 1,
                          "name": "감자볶음",
                          "thumbnail": "http://example.com/image.jpg",
                          "category": "반찬",
                          "price": 5000
                        }
                      ],
                      "message": "요청 조건에 맞는 요리가 모두 조회되었습니다.",
                      "status": 200
                    }
                """))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (페이지 번호/크기/카테고리/정렬값 등 파라미터 오류)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "data": null,
                      "message": "페이지 번호나 크기가 유효하지 않습니다.",
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
            @ApiResponse(responseCode = "400", description = "잘못된 정렬 조건 입력",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "data": null,
                      "message": "정렬 조건이 유효하지 않습니다.",
                      "status": 400
                    }
                """))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "data": null,
                      "message": "서버 처리 중 오류가 발생했습니다.",
                      "status": 500
                    }
                """))),
            @ApiResponse(responseCode = "204", description = "조건에 맞는 요리 데이터가 존재하지 않음",
                    content = @Content)
    })
    @GetMapping("/cooks")
    ResponseEntity<?> getCooks(
            @Parameter(description = "카테고리 (예: 반찬, 국, 찌개)", required = false)
            @RequestParam(required = false) String category,
            @Parameter(description = "검색 키워드", required = false)
            @RequestParam(required = false) String keyword,
            @Parameter(description = "정렬 조건 (예: order, updatedAt)", required = false)
            @RequestParam(required = false) String sort,
            @Parameter(description = "페이지 번호 (1부터 시작)", required = true)
            @RequestParam Long pageNo,
            @Parameter(description = "페이지 크기", required = true)
            @RequestParam Long numOfRows
    );
}
