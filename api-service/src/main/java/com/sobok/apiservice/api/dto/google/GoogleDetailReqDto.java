package com.sobok.apiservice.api.dto.google;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Google ID 토큰 요청 DTO")
public class GoogleDetailReqDto {

    @Schema(description = "Google ID 토큰", example = "eyJhbGciOiJSUzI1NiIsImtpZCI6Ij...")
    private String id_token;
}