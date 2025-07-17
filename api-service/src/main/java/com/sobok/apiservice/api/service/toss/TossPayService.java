package com.sobok.apiservice.api.service.toss;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sobok.apiservice.api.client.PaymentFeignClient;
import com.sobok.apiservice.api.dto.toss.TossCancelReqDto;
import com.sobok.apiservice.api.dto.toss.TossPayReqDto;
import com.sobok.apiservice.api.dto.toss.TossPayResDto;
import com.sobok.apiservice.common.exception.CustomException;
import feign.FeignException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class TossPayService {
    private final RestClient.Builder restClientBuilder;
    private final PaymentFeignClient paymentFeignClient;
    private final ObjectMapper objectMapper;

    private RestClient restClient;
    private String authHeader;

    private static final String tossPayBaseUrl = "https://api.tosspayments.com";
    private static final String widgetSecretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";

    @PostConstruct
    private void init() {
        // RestClient 객체 생성
        this.restClient = restClientBuilder.baseUrl(tossPayBaseUrl).build();

        // Authorization 헤더 생성
//        this.authHeader = "Basic " + Base64.getEncoder()
//                .encodeToString((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));

        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + new String(encodedBytes);
    }

    /**
     * 토스페이 결제
     */
    public TossPayResDto confirmPayment(TossPayReqDto reqDto) {
        log.info("결제 확인 및 주문 등록 시작 | orderId = {}", reqDto.getOrderId());

        // TossPay로 부터 결제가 완료되었는지 검증
        TossPayResDto resDto = null;
        boolean isError = false;
        try {
            resDto = tosspayConfirm(reqDto);
        } catch (ParseException e) {
            log.error("파싱 실패 : {}", e.getMessage(), e);
            isError = true;
        } catch (IOException e) {
            log.error("IO 실패 : {}", e.getMessage(), e);
            isError = true;
        } catch (JSONException e) {
            log.error("JSON 실패 : {}", e.getMessage(), e);
            isError = true;
        }

        if (isError) {
            throw new CustomException("승인 과정에서 문제가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            // payment-service에 주문 정보 등록
            paymentFeignClient.registerPayment(resDto);
        } catch (Exception e) {
            isError = true;
        }

        // 결제 취소 로직
        if (isError) {
            try {
                // paymentKey를 사용한 결제 취소
                cancelPayment(resDto.getPaymentKey(), "서비스 내부 주문 등록 오류");
                log.error("결제 정보를 등록하는 과정에서 오류가 발생함. | orderId = {}", reqDto.getOrderId());
            } catch (CustomException e) {
                log.error(e.getMessage(), e);
                throw e;
            } catch (Exception e) {
                log.error("결제를 취소하는 과정에서 문제가 발생했습니다.");
                throw new CustomException("결제를 취소하는 과정에서 문제가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
            } finally {
                // 결국 취소는 해야함.
                paymentFeignClient.cancelPayment(reqDto.getOrderId());
            }

            throw new CustomException("결제 정보를 등록하는 과정에서 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return resDto;
    }

    private TossPayResDto tosspayConfirm(TossPayReqDto reqDto) throws ParseException, IOException, JSONException {
        JSONObject obj = new JSONObject();
        obj.put("orderId", reqDto.getOrderId());
        obj.put("amount", reqDto.getAmount());
        obj.put("paymentKey", reqDto.getPaymentKey());

        // 토스페이먼츠 API는 시크릿 키를 사용자 ID로 사용하고, 비밀번호는 사용하지 않습니다.
        // 비밀번호가 없다는 것을 알리기 위해 시크릿 키 뒤에 콜론을 추가합니다.
        String widgetSecretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);

        // 결제를 승인하면 결제수단에서 금액이 차감돼요.
        URL url = new URL("https://api.tosspayments.com/v1/payments/confirm");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", authorizations);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(obj.toString().getBytes("UTF-8"));

        int code = connection.getResponseCode();
        boolean isSuccess = code == 200;

        InputStream responseStream = isSuccess ? connection.getInputStream() : connection.getErrorStream();

        // 결제 성공 및 실패 비즈니스 로직을 구현하세요.
        Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
        JSONParser parser = new JSONParser(reader);
        LinkedHashMap<String, String> jsonObject = (LinkedHashMap<String, String>) parser.parse();
        responseStream.close();

        return TossPayResDto.builder()
                .orderId(reqDto.getOrderId())
                .paymentKey(reqDto.getPaymentKey())
                .method((String) jsonObject.get("method"))
                .build();

    }

    /**
     * 토스 페이 결제 확인
     */
    private TossPayResDto getTossPayConfirm(TossPayReqDto reqDto) {
        log.info("토스 페이 결제 확인 시작 | orderId = {}", reqDto.getOrderId());

        log.info("authHeader = {}", authHeader);

        log.info("토스에 요청 보내기 | TossPay 요청 DTO: {}", reqDto);
        // 결제를 승인하면 결제수단에서 금액이 차감돼요.
        TossPayResDto resDto = restClient.post()
                .uri("/v1/payment/confirm")
                .header("Authorization", authHeader)
                .body(reqDto)
                .accept(MediaType.APPLICATION_JSON)
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    log.error(res.getStatusText());
                    try (InputStream inputStream = res.getBody()) {
                        String responseBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                        log.error("4xx Error - Status: {}, Body: {}", res.getStatusCode(), responseBody);
                    } catch (IOException e) {
                        log.error("응답 바디 읽기 실패: {}", e.getMessage());
                    }
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
    private void cancelPaymentA(String paymentKey, String cancelReason) {
        log.info("토스 페이 결제 취소 시작 | paymentKey = {}, cancelReason : {}", paymentKey, cancelReason);

        TossCancelReqDto requestDto = new TossCancelReqDto(cancelReason);

        // 결제를 승인하면 결제수단에서 금액이 차감돼요.
        TossPayResDto resDto = restClient.post()
                .uri("/v1/payments/" + paymentKey + "/cancel")
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

    private void cancelPayment(String paymentKey, String cancelReason) throws ParseException, IOException, JSONException {
        log.info("토스 페이 결제 취소 시작 | paymentKey = {}, cancelReason : {}", paymentKey, cancelReason);

        JSONObject obj = new JSONObject();
        obj.put("paymentKey", paymentKey);
        obj.put("cancelReason", cancelReason);

        // 토스페이먼츠 API는 시크릿 키를 사용자 ID로 사용하고, 비밀번호는 사용하지 않습니다.
        // 비밀번호가 없다는 것을 알리기 위해 시크릿 키 뒤에 콜론을 추가합니다.
        String widgetSecretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);

        // 결제를 승인하면 결제수단에서 금액이 차감돼요.
        URL url = new URL("https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", authorizations);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(obj.toString().getBytes("UTF-8"));

        int code = connection.getResponseCode();
        boolean isSuccess = code == 200;

        InputStream responseStream = isSuccess ? connection.getInputStream() : connection.getErrorStream();

        // 결제 성공 및 실패 비즈니스 로직을 구현하세요.
        Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
        JSONParser parser = new JSONParser(reader);
        LinkedHashMap<String, String> jsonObject = (LinkedHashMap<String, String>) parser.parse();
        responseStream.close();

        if(!isSuccess) {
            log.error("토스 페이 결제 취소 실패 ㅠㅠ");
            throw new CustomException("토스 페이 결제 취소가 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
