package com.sobok.paymentservice.payment.service.cart;

import com.sobok.paymentservice.common.dto.TokenUserInfo;
import com.sobok.paymentservice.common.exception.CustomException;
import com.sobok.paymentservice.payment.dto.cart.DeleteCartReqDto;
import com.sobok.paymentservice.payment.entity.CartCook;
import com.sobok.paymentservice.payment.repository.CartCookRepository;
import com.sobok.paymentservice.payment.repository.CartIngreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartCookService {
    private final CartCookRepository cartCookRepository;
    private final CartIngreRepository cartIngreRepository;

    private final CartCookVerifier cartCookVerifier;


    @Transactional
    public Long editCartCook(TokenUserInfo userInfo, Long cartCookId, Integer count) {
        // 1. 사용자 검증
        CartCook cart = cartCookVerifier.verifyCartCook(userInfo.getUserId(), cartCookId);

        // 2. 장바구니 요리 수량 수정
        cart.changeCount(count);

        // 3. DB 저장
        cartCookRepository.save(cart);

        return cartCookId;
    }

    @Transactional
    public Long deleteCartCook(TokenUserInfo userInfo, Long cartCookId) {
        // 1. 사용자 검증
        CartCook cart = cartCookVerifier.verifyCartCook(userInfo.getUserId(), cartCookId);

        // 2. 장바구니 요리 추가 식재료 제거
        cartIngreRepository.deleteByUnpaidCartCookId(cartCookId);

        // 3. 장바구니 요리 제거
        cartCookRepository.deleteUnpaidById(cart.getId());

        return cartCookId;
    }


    @Transactional
    public void deleteCartCookList(TokenUserInfo userInfo, DeleteCartReqDto reqDto) {
        // 1. 사용자 입력 검증
        List<Long> reqList = reqDto.getCartCookIdList();
        if (reqList == null || reqList.isEmpty()) {
            throw new CustomException("삭제할 항목이 없습니다.", HttpStatus.BAD_REQUEST);
        }

        // 2. 입력 리스트를 바탕으로 삭제할 ID 리스트 생성
        List<Long> deleteIdList = cartCookRepository.findByIdInAndUserId(reqList, userInfo.getUserId())
                .stream()
                .map(cartCook -> {
                    // 결제된 요리에 접근한다면 예외 반환
                    if (cartCook.getPaymentId() != null) {
                        throw new CustomException("잘못된 접근입니다.", HttpStatus.FORBIDDEN);
                    }
                    return cartCook.getId();
                })
                .toList();

        // 2. 모든 장바구니 요리 추가 식재료 제거
        cartIngreRepository.deleteByUnpaidCartCookIdList(deleteIdList);

        // 3. 모든 장바구니 요리 삭제
        cartCookRepository.deleteByIdIn(deleteIdList);
    }
}
