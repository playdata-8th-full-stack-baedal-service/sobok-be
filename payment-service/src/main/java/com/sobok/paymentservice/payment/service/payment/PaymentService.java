package com.sobok.paymentservice.payment.service.payment;

import com.sobok.paymentservice.common.enums.OrderState;
import com.sobok.paymentservice.common.exception.CustomException;
import com.sobok.paymentservice.payment.client.ShopFeignClient;
import com.sobok.paymentservice.payment.dto.payment.AdminPaymentResDto;
import com.sobok.paymentservice.payment.dto.payment.PaymentRegisterReqDto;
import com.sobok.paymentservice.payment.dto.payment.ShopAssignDto;
import com.sobok.paymentservice.payment.dto.payment.TossPayRegisterReqDto;
import com.sobok.paymentservice.payment.dto.response.CartCookResDto;
import com.sobok.paymentservice.payment.dto.response.CartIngredientResDto;
import com.sobok.paymentservice.payment.entity.CartCook;
import com.sobok.paymentservice.payment.entity.Payment;
import com.sobok.paymentservice.payment.repository.CartCookRepository;
import com.sobok.paymentservice.payment.repository.CartIngreRepository;
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
    private final ShopFeignClient shopFeignClient;
    private final CartIngreRepository CartIngreRepository;


    /**
     * 결제 사전 정보 등록
     */
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

    /**
     * 결제 완료 등록
     */
    public void completePayment(TossPayRegisterReqDto reqDto) {
        log.info("결제 완료 정보 등록 시작 | Request : {}", reqDto);

        // Pending 상태의 Payment ID 가져오기
        Payment payment = paymentRepository.getPendingPaymentByOrderId(reqDto.getOrderId(), OrderState.ORDER_PENDING).orElseThrow(
                () -> new CustomException("해당하는 결제 내역이 존재하지 않습니다.", HttpStatus.BAD_REQUEST)
        );

        // payment 나머지 정보 세팅
        payment.completePayment(reqDto.getPaymentKey(), reqDto.getMethod(), OrderState.ORDER_COMPLETE);

        log.info("결제 정보 저장 완료 | OrderId : {}", payment.getOrderId());

        // Payment 객체 저장
        paymentRepository.save(payment);

        // 가게 자동 배정 시작
        shopFeignClient.assignNearestShop(new ShopAssignDto(payment.getUserAddressId(), payment.getId()));

        // payment 상태 바꾸기
        payment.nextState();

        // State 바꿔서 다시 저장
        paymentRepository.save(payment);

        log.info("주문 완료 처리 및 가게 자동 배정 완료");
    }

    /**
     * 결제 취소
     */
    public void cancelPayment(String orderId) {
        log.info("결제 취소 시작 | orderId : {}", orderId);

        // Pending 상태의 Payment 객체 가져오기
        Payment payment = paymentRepository.getPendingPaymentByOrderId(orderId, OrderState.ORDER_PENDING).orElseThrow(
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
            cartCook.detachFromPayment();
            cartCookRepository.save(cartCook);
        }

        // payment 객체 삭제
        paymentRepository.delete(payment);

        log.info("결제 취소 성공 | orderId : {}, paymentId : {}", orderId, payment.getId());
    }

    /**
     * 주문 전체 조회 (결제)
     */
    public List<AdminPaymentResDto> getAllPaymentsForAdmin() {
        return paymentRepository.findAll().stream()
                .map(payment -> AdminPaymentResDto.builder()
                        .id(payment.getId())
                        .orderId(payment.getOrderId())
                        .totalPrice(payment.getTotalPrice())
                        .payMethod(payment.getPayMethod())
                        .orderState(payment.getOrderState())
                        .createdAt(payment.getCreatedAt())
                        .userAddressId(payment.getUserAddressId())
                        .build())
                .toList();
    }

    /**
     * 결제 정보에 맞는 요리 이름 조회용
     */
    public List<Long> getCookIdsByPaymentId(Long paymentId) {
        return cartCookRepository.findByPaymentId(paymentId)
                .stream()
                .map(CartCook::getCookId)
                .distinct()
                .toList();
    }

    /**
     * 결제 Id에 해당하는 장바구니 요리 목록을 조회하여 DTO로 변환
     */
    public List<CartCookResDto> getCartCooksByPaymentId(Long paymentId) {
        return cartCookRepository.findByPaymentId(paymentId).stream()
                .map(cook -> CartCookResDto.builder()
                        .id(cook.getId())
                        .cookId(cook.getCookId())
                        .quantity(cook.getCount())
                        .build())
                .toList();
    }

    /**
     * 장바구니 요리 Id에 해당하는 재료 목록을 조회하여 DTO로 변환
     */
    public List<CartIngredientResDto> getIngredientsByCartCookId(Long cartCookId) {
        return CartIngreRepository.findByCartCookId(cartCookId).stream()
                .map(ingre -> CartIngredientResDto.builder()
                        .ingreId(ingre.getIngreId())
                        .defaultIngre(ingre.getDefaultIngre())
                        .unitQuantity(ingre.getUnitQuantity())
                        .build())
                .toList();
    }

}
