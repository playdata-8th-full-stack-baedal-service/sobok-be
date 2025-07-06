package com.sobok.apiservice.api.dto.address;

import lombok.Data;

import java.util.List;

@Data
public class KakaoLocDto {
    List<Document> documents;

    @Data
    public static class Document {
        private String x;
        private String y;
    }
}
