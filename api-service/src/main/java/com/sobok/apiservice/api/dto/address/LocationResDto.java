package com.sobok.apiservice.api.dto.address;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "위치 응답 DTO")
public class LocationResDto {

    @Schema(description = "위도", example = "37.49794")
    Double latitude;

    @Schema(description = "경도", example = "127.02758")
    Double longitude;
}