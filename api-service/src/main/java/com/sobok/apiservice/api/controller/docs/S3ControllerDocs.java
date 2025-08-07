package com.sobok.apiservice.api.controller.docs;

import com.sobok.apiservice.common.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "S3 이미지 관리", description = "S3 이미지 업로드, 변경, 삭제 API")
public interface S3ControllerDocs {

    @Operation(
            summary = "S3 이미지 삭제",
            description = "S3에 저장된 이미지 파일을 삭제합니다. 삭제할 파일의 key를 전달해야 합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "success": true,
                                      "data": "images/abc.jpg",
                                      "message": "S3의 파일이 성공적으로 삭제되었습니다.",
                                      "status": 200
                                    }
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "500", description = "서버 에러",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "success": false,
                                      "data": null,
                                      "message": "S3 이미지를 삭제하는 과정에서 오류가 발생했습니다.",
                                      "status": 500
                                    }
                                    """)
                    )
            )
    })
    ResponseEntity<?> deleteS3Image(
            @Parameter(description = "삭제할 이미지의 S3 key", required = true, in = ParameterIn.QUERY, example = "temp/profile/uuid_filename.jpg")
            String key
    );


    @Operation(
            summary = "S3 이미지 업로드",
            description = "이미지를 S3에 임시 업로드합니다. 10분 이내에 register 과정을 거쳐야 합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "업로드 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "success": true,
                                      "data": "https://cdn.example.com/temp/profile/uuid_filename.jpg",
                                      "message": "S3에 파일이 정상적으로 업로드되었습니다.",
                                      "status": 200
                                    }
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 잘못된 카테고리, 빈 파일 등)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "success": false,
                                      "data": null,
                                      "message": "유효하지 않은 카테고리 입력입니다.",
                                      "status": 400
                                    }
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "500", description = "서버 에러",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class),
                            examples = @ExampleObject(value = """
                                     {
                                      "success": false,
                                      "data": null,
                                      "message": "서버 내부 오류가 발생했습니다.",
                                      "status": 500
                                    }
                                    """)
                    )
            )
    })
    ResponseEntity<?> putS3Image(
            @Parameter(description = "업로드할 이미지 파일", required = true)
            MultipartFile image,

            @Parameter(description = "이미지 카테고리 (예: profile, product 등)", required = true, example = "profile")
            String category
    );

}
