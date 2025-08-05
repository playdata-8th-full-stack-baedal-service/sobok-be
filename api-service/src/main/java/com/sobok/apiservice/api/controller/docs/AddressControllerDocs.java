package com.sobok.apiservice.api.controller.docs;


import com.sobok.apiservice.api.dto.address.LocationResDto;
import com.sobok.apiservice.common.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "주소 변환", description = "도로명 주소 → 좌표 변환 API")
public interface AddressControllerDocs {

    @Operation(
            summary = "도로명 주소 → 좌표 변환",
            description = "도로명 주소를 위도, 경도로 변환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좌표 변환 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LocationResDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "latitude": 37.5665,
                                      "longitude": 126.978
                                    }
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "500", description = "좌표 변환 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "success": false,
                                      "data": null,
                                      "message": "주소 변환 과정에서 오류가 발생했습니다.",
                                      "status": 500
                                    }
                                    """)
                    )
            )
    })
    LocationResDto convertAddress(
            @Parameter(description = "도로명 전체 주소", required = true, example = "서울특별시 중구 세종대로 110")
            String roadFull
    );
}
