package com.sobok.apiservice.api.dto.S3;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class S3ImgMetaDto {

    @Schema(description = "S3에 저장된 이미지 파일 이름", example = "profile_1698392000000.png")
    String fileName;

    @Schema(description = "이미지 분류 (예: profile, banner, etc.)", example = "profile")
    String category;
}