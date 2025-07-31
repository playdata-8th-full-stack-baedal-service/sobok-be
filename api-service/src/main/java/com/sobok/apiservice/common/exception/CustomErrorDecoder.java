package com.sobok.apiservice.common.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Slf4j
public class CustomErrorDecoder implements ErrorDecoder {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        String body = extractBody(response);
        String message = extractInnermostMessage(body);

        log.warn("{} 요청이 실패했습니다. status: {}, url: {}, message: {}",
                methodKey, response.status(), response.request().url(), message);

        switch (response.status()) {
            case 400:
                return new CustomException(message, HttpStatus.BAD_REQUEST);
            case 401:
                return new CustomException(message, HttpStatus.UNAUTHORIZED);
            case 403:
                return new CustomException(message, HttpStatus.FORBIDDEN);
            case 404:
                return new CustomException(message, HttpStatus.NOT_FOUND);
            case 500:
                return new CustomException(message, HttpStatus.INTERNAL_SERVER_ERROR);
            case 503:
                return new CustomException(message, HttpStatus.SERVICE_UNAVAILABLE);
            default:
                return new CustomException("[" + methodKey + "] 호출 실패: " + response.status() + " - " + message,
                        HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String extractBody(Response response) {
        try {
            if (response.body() != null) {
                return new BufferedReader(new InputStreamReader(response.body().asInputStream(), StandardCharsets.UTF_8))
                        .lines()
                        .collect(Collectors.joining("\n"));
            }
        } catch (Exception e) {
            return "응답 본문을 읽는 중 오류 발생: " + e.getMessage();
        }
        return "응답 본문이 없습니다.";
    }

    private String extractInnermostMessage(String body) {

        if (body == null || body.isBlank()) return "응답 본문이 없습니다.";

        // JSON 구조가 아니면 그대로 반환
        if (!body.trim().startsWith("{")) {
            return body.trim();
        }

        try {
            JsonNode root = mapper.readTree(body);

            // 1차 message
            String outerMessage = root.has("message") ? root.get("message").asText() : null;
            if (outerMessage == null) return "알 수 없는 오류";

            // 2차 중첩 JSON 파싱 시도
            int jsonStart = outerMessage.indexOf("{");
            int jsonEnd = outerMessage.lastIndexOf("}");

            if (jsonStart != -1 && jsonEnd != -1 && jsonEnd > jsonStart) {
                String innerJsonStr = outerMessage.substring(jsonStart, jsonEnd + 1);

                try {
                    JsonNode inner = mapper.readTree(innerJsonStr);
                    if (inner.has("message")) {
                        return inner.get("message").asText();  // 진짜 메시지 반환
                    }
                } catch (Exception e) {
                    // 내부 JSON 파싱 실패하면 outerMessage 그대로 반환
                    return outerMessage;
                }
            }

            return outerMessage;
        } catch (Exception e) {
            return "에러 메시지 파싱 실패: " + e.getMessage();
        }
    }
}
