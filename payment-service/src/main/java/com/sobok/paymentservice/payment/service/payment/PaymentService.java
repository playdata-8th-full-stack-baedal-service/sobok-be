package com.sobok.paymentservice.payment.service.payment;

import com.sobok.paymentservice.common.enums.OrderState;
import com.sobok.paymentservice.common.exception.CustomException;
import com.sobok.paymentservice.payment.dto.payment.PaymentRegisterReqDto;
import com.sobok.paymentservice.payment.dto.payment.TossPayRegisterReqDto;
import com.sobok.paymentservice.payment.entity.CartCook;
import com.sobok.paymentservice.payment.entity.Payment;
import com.sobok.paymentservice.payment.repository.CartCookRepository;
import com.sobok.paymentservice.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final CartCookRepository cartCookRepository;


    public Long registerPayment(PaymentRegisterReqDto reqDto) {
        log.info("결제 사전 정보 등록 시작 | Request : {}", reqDto);

        // Payment 객체 생성
        Payment payment = Payment.builder()
                .orderState(OrderState.ORDER_PENDING)
                .totalPrice(reqDto.getTotalPrice())
                .riderRequest(reqDto.getRiderRequest())
                .userAddressId(reqDto.getUserAddressId())
                .orderId(reqDto.getOrderId())
                .build();

        // 결제 사전 정보 저장
        paymentRepository.save(payment);

        // 요리 결제 정보 등록
        for (Long cartCookId : reqDto.getCartCookIdList()) {
            // 장바구니에 들어있는 CartCook 꺼내오기
            CartCook cartCook = cartCookRepository.findUnpaidCartById(cartCookId).orElseThrow(
                    () -> new CustomException("해당하는 장바구니 요리가 없습니다.", HttpStatus.BAD_REQUEST)
            );

            // paymentId 세팅 후 저장
            cartCook.setPaymentId(payment.getId());
            cartCookRepository.save(cartCook);
        }

        log.info("결제 사전 정보 등록 완료 | 주문번호 : {}", payment.getId());

        // 주문 번호 반환
        return payment.getId();
    }

    public void completePayment(TossPayRegisterReqDto reqDto) {
        log.info("결제 완료 정보 등록 시작 | Request : {}", reqDto);

        // Payment ID 가져오기
        Payment payment = paymentRepository.getPaymentByOrderId(reqDto.getOrderId()).orElseThrow(
                () -> new CustomException("해당하는 결제 내역이 존재하지 않습니다.", HttpStatus.BAD_REQUEST)
        );

        // payment 나머지 정보 세팅
        payment.completePayment(reqDto.getPaymentKey(), reqDto.getMethod(), OrderState.ORDER_COMPLETE);

        log.info("결제 정보 저장 완료 | OrderId : {}", payment.getOrderId());

        // Payment 객체 저장
        paymentRepository.save(payment);

        // TODO MQ로 비동기 가게 배정
    }

    /**
     * 결제 취소
     */
    public void cancelPayment(String orderId) {
        log.info("결제 취소 시작 | orderId : {}", orderId);

        // payment 객체 가져오기
        Payment payment = paymentRepository.getPaymentByOrderId(orderId).orElseThrow(
                () -> new CustomException("해당하는 결제 내역이 존재하지 않습니다.", HttpStatus.BAD_REQUEST)
        );

        // Cart Cook 리스트 가져오기
        List<CartCook> cartCookList = cartCookRepository.findByPaymentId(payment.getId());
        if(cartCookList.isEmpty()) {
            log.error("결제 내역에 해당하는 카트 정보가 존재하지 않습니다. | payment id : {}", payment.getId());
            throw new CustomException("결제 내역에 해당하는 카트 정보가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        // 결제 기록 삭제
        for (CartCook cartCook : cartCookList) {
            // Payment Id를 null로 바꾸기
            cartCook.setPaymentId(null);
            cartCookRepository.save(cartCook);
        }

        // payment 객체 삭제
        paymentRepository.delete(payment);

        log.info("결제 취소 성공 | orderId : {}, paymentId : {}", orderId, payment.getId());
    }
}
