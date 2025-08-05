package com.sobok.apiservice.api.dto.address;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "카카오 위치 정보 응답 DTO")
public class KakaoLocDto {

    @Schema(description = "위치 정보 문서 리스트")
    List<Document> documents;

    @Data
    @Schema(description = "카카오 위치 정보 단일 문서")
    public static class Document {

        @Schema(description = "경도 (x 좌표)", example = "127.02758")
        private String x;

        @Schema(description = "위도 (y 좌표)", example = "37.49794")
        private String y;
    }
}
