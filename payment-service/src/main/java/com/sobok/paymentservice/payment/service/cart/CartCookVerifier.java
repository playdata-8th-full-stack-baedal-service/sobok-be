package com.sobok.paymentservice.payment.service.cart;

import com.sobok.paymentservice.common.exception.CustomException;
import com.sobok.paymentservice.payment.entity.CartCook;
import com.sobok.paymentservice.payment.repository.CartCookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartCookVerifier {
    private final CartCookRepository cartCookRepository;

    public CartCook verifyCartCook(Long userId, Long cartCookId) {
        return cartCookRepository.getCartCooksByUserIdAndId(userId, cartCookId).orElseThrow(
                () -> new CustomException("해당하는 장바구니 요리가 없습니다.", HttpStatus.NOT_FOUND)
        );
    }
}
