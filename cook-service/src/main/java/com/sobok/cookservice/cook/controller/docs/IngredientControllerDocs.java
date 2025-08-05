package com.sobok.cookservice.cook.controller.docs;

import com.sobok.cookservice.common.dto.CommonResponse;
import com.sobok.cookservice.cook.dto.request.IngreEditReqDto;
import com.sobok.cookservice.cook.dto.request.IngreReqDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface IngredientControllerDocs {

    @Operation(summary = "관리자 재료 등록")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "재료 등록 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "success": true,
                                      "data": "감자",
                                      "message": "식재료가 등록되었습니다.",
                                      "status": 200
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "이미 등록된 재료",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "success": false,
                                      "data": null,
                                      "message": "이미 등록된 재료입니다.",
                                      "status": 400
                                    }
                                    """)))
    })
    ResponseEntity<?> ingreRegister(
            @Parameter(description = "등록할 식재료 DTO", required = true)
            @RequestBody IngreReqDto reqDto
    );

    @Operation(summary = "키워드로 식재료 검색")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "키워드 검색 결과 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "success": true,
                                      "data": [
                                        {
                                          "id": 1,
                                          "ingreName": "감자",
                                          "price": 1000,
                                          "origin": "국산",
                                          "unit": 1
                                        }
                                      ],
                                      "message": "키워드로 검색한 식재료 결과입니다.",
                                      "status": 200
                                    }
                                    """))),
            @ApiResponse(responseCode = "204", description = "검색 결과 없음", content = @Content)
    })
    ResponseEntity<?> ingreSearch(
            @Parameter(description = "검색 키워드", required = true)
            @RequestParam String keyword
    );

    @Operation(summary = "전체 식재료 조회 (관리자용)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "전체 식재료 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "success": true,
                                      "data": [
                                        {
                                          "id": 1,
                                          "ingreName": "감자",
                                          "price": 1000,
                                          "origin": "국산",
                                          "unit": 1
                                        }
                                      ],
                                      "message": "전체 식재료 결과입니다.",
                                      "status": 200
                                    }
                                    """))),
            @ApiResponse(responseCode = "204", description = "식재료 없음", content = @Content)
    })
    ResponseEntity<?> allSearch();

    @Operation(summary = "식재료 정보 수정 (관리자용)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "식재료 정보 수정 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "success": true,
                                      "data": 1,
                                      "message": "해당 식재료 정보가 수정되었습니다.",
                                      "status": 200
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "존재하지 않는 식재료 ID",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "success": false,
                                      "data": null,
                                      "message": "해당 식재료가 존재하지 않습니다.",
                                      "status": 400
                                    }
                                    """)))
    })
    ResponseEntity<?> ingreEdit(
            @Parameter(description = "수정할 식재료 정보 DTO", required = true)
            @RequestBody IngreEditReqDto newReqDto
    );
}
