package com.sobok.apiservice.api.service.toss;

import com.sobok.apiservice.api.client.PaymentFeignClient;
import com.sobok.apiservice.api.dto.toss.TossCancelReqDto;
import com.sobok.apiservice.api.dto.toss.TossPayReqDto;
import com.sobok.apiservice.api.dto.toss.TossPayResDto;
import com.sobok.apiservice.common.exception.CustomException;
import feign.FeignException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@Slf4j
@RequiredArgsConstructor
public class TossPayService {
    private final RestClient.Builder restClientBuilder;
    private final PaymentFeignClient paymentFeignClient;

    private RestClient restClient;
    private String authHeader;

    private static final String tossPayBaseUrl = "https://api.tosspayments.com";
    private static final String widgetSecretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";

    @PostConstruct
    private void init() {
        // RestClient 객체 생성
        this.restClient = restClientBuilder.baseUrl(tossPayBaseUrl).build();

        // Authorization 헤더 생성
        this.authHeader = "Basic " + Base64.getEncoder()
                .encodeToString((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 토스페이 결제
     */
    public TossPayResDto confirmPayment(TossPayReqDto reqDto) {
        log.info("결제 확인 및 주문 등록 시작 | orderId = {}", reqDto.getOrderId());

        // TossPay로 부터 결제가 완료되었는지 검증
        TossPayResDto resDto = getTossPayConfirm(reqDto);

        try {
            // payment-service에 주문 정보 등록
            paymentFeignClient.registerPayment(resDto);
        } catch (FeignException e) {
            // paymentKey를 사용한 결제 취소
            cancelPayment(resDto.getPaymentKey(), "서비스 내부 주문 등록 오류");

            log.error("결제 정보를 등록하는 과정에서 오류가 발생함. | orderId = {}", reqDto.getOrderId());
            throw new CustomException("결제 정보를 등록하는 과정에서 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return resDto;
    }

    /**
     * 토스 페이 결제 확인
     */
    private TossPayResDto getTossPayConfirm(TossPayReqDto reqDto) {
        log.info("토스 페이 결제 확인 시작 | orderId = {}", reqDto.getOrderId());

        log.info("토스에 요청 보내기 | TossPay 요청 DTO: {}", reqDto);
        // 결제를 승인하면 결제수단에서 금액이 차감돼요.
        TossPayResDto resDto = restClient.post()
                .uri("/v1/payment/confirm")
                .header("Authorization", authHeader)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(reqDto)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    log.error(res.getStatusText());
                    throw new CustomException("토스 페이 요청 중 문제가 발생했습니다.", HttpStatus.BAD_REQUEST);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    log.error(res.getStatusText());
                    throw new CustomException("토스 페이 요청 중 문제가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
                })
                .body(TossPayResDto.class);

        if (resDto == null) {
            throw new CustomException("토스 페이에서 아무런 응답도 받지 못했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        log.info("토스 결제 성공 | paymentKey : {}, method : {}", resDto.getPaymentKey(), resDto.getMethod());
        return resDto;
    }

    /**
     * 토스 페이 결제 취소
     */
    private void cancelPayment(String paymentKey, String cancelReason) {
        log.info("토스 페이 결제 취소 시작 | paymentKey = {}, cancelReason : {}", paymentKey, cancelReason);

        TossCancelReqDto requestDto = new TossCancelReqDto(cancelReason);

        // 결제를 승인하면 결제수단에서 금액이 차감돼요.
        TossPayResDto resDto = restClient.post()
                .uri("/v1/payments/" + paymentKey + "/confirm")
                .header("Authorization", authHeader)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestDto)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    log.error(res.getStatusText());
                    throw new CustomException("토스 페이 요청 중 문제가 발생했습니다.", HttpStatus.BAD_REQUEST);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    log.error(res.getStatusText());
                    throw new CustomException("토스 페이 요청 중 문제가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
                })
                .body(TossPayResDto.class);

        if (resDto == null) {
            throw new CustomException("토스 페이에서 아무런 응답도 받지 못했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        log.info("토스 결제 취소 성공 | paymentKey : {}, method : {}", resDto.getPaymentKey(), resDto.getMethod());
    }
}
