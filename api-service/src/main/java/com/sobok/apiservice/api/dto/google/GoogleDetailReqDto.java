package com.sobok.apiservice.api.dto.google;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GoogleDetailReqDto {
    private String id_token;
}
