package com.sobok.apiservice.api.service.toss;

import com.sobok.apiservice.api.dto.TossPayReqDto;
import com.sobok.apiservice.api.dto.TossPayResDto;
import com.sobok.apiservice.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@Slf4j
@RequiredArgsConstructor
public class TossPayService {
    private final RestClient restClient;

    /**
     * 토스페이 결제
     */
    public TossPayResDto confirmPayment(@RequestBody TossPayReqDto reqDto) {
        log.info("토스 페이 결제 확인 시작 | orderId = {}", reqDto.getOrderId());

        // 토스페이먼츠 API는 시크릿 키를 사용자 ID로 사용하고, 비밀번호는 사용하지 않습니다.
        // 비밀번호가 없다는 것을 알리기 위해 시크릿 키 뒤에 콜론을 추가합니다.
        String widgetSecretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);


        log.info("토스에 요청 보내기 | TossPay 요청 DTO: {}", reqDto);
        // 결제를 승인하면 결제수단에서 금액이 차감돼요.
        TossPayResDto resDto = restClient.post()
                .uri("/v1/payment/confirm")
                .header("Authorization", authorizations)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(reqDto)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    log.error(res.getStatusText());
                })
                .body(TossPayResDto.class);

        log.info("토스 결제 성공 | mid : {}", resDto.getMid());
        return resDto;
    }
}
