package com.sobok.paymentservice.payment.service;

import com.sobok.paymentservice.common.dto.TokenUserInfo;
import com.sobok.paymentservice.common.exception.CustomException;
import com.sobok.paymentservice.payment.client.CookFeignClient;
import com.sobok.paymentservice.payment.dto.cart.CartAddCookReqDto;
import com.sobok.paymentservice.payment.dto.response.CookDetailResDto;
import com.sobok.paymentservice.payment.dto.response.IngredientResDto;
import com.sobok.paymentservice.payment.dto.response.PaymentItemResDto;
import com.sobok.paymentservice.payment.dto.response.PaymentResDto;
import com.sobok.paymentservice.payment.entity.CartCook;
import com.sobok.paymentservice.payment.entity.CartIngredient;
import com.sobok.paymentservice.payment.repository.CartCookRepository;
import com.sobok.paymentservice.payment.repository.CartIngreRepository;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartService {
    private final CookFeignClient cookFeignClient;
    private final CartCookRepository cartCookRepository;
    private final CartIngreRepository cartIngreRepository;

    /**
     * 장바구니 추가
     * 1. cook service에서 요리를 꺼내와 요리 기본 식재료 가져오기
     * 2. cart_cook 데이터 저장
     * 3. cart_ingre 데이터 저장
     */
    @Transactional
    public void addCartCook(CartAddCookReqDto reqDto) {
        log.info("장바구니 추가 시작");

        // 기본 식재료 가져오기 (key : ingreId, value : unitQuantity)
        Map<Long, Integer> defaultIngreList = null;
        try {
            defaultIngreList = cookFeignClient.getDefaultIngreInfoList(reqDto.getCookId());
        } catch (FeignException e) {
            log.error("Cook Service로 Feign 과정 중 오류 발생");
            throw new CustomException("기본 식재료를 가져오는 과정에서 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // 기본 식재료가 null 값이 왔다면 예외 처리
        if (defaultIngreList == null) {
            throw new CustomException("해당하는 요리가 존재하지 않거나 기본 식재료가 없습니다.", HttpStatus.NOT_FOUND);
        }

        // 장바구니 요리 저장
        log.info("장바구니 요리 저장 시작");
        CartCook cartCook = CartCook.builder()
                .userId(reqDto.getUserId())
                .cookId(reqDto.getCookId())
                .count(reqDto.getCount())
                .build();
        cartCook = cartCookRepository.save(cartCook);

        // 기본 요리 저장
        log.info("기본 식재료 저장 시작");
        for (Long key : defaultIngreList.keySet()) {
            CartIngredient cartIngre = CartIngredient.builder()
                    .cartCookId(cartCook.getId())
                    .ingreId(key)
                    .defaultIngre("Y")
                    .unitQuantity(defaultIngreList.get(key))
                    .build();
            cartIngreRepository.save(cartIngre);
        }

        // 추가 요리 저장 (따로 분리하여 겹치는 식재료 추가 등록도 가능하게 함)
        log.info("추가 식재료 저장 시작");
        for (CartAddCookReqDto.AdditionalIngredient ingre : reqDto.getAdditionalIngredients()) {
            CartIngredient cartIngre = CartIngredient.builder()
                    .cartCookId(cartCook.getId())
                    .ingreId(ingre.getIngreId())
                    .defaultIngre("N")
                    .unitQuantity(ingre.getUnitQuantity())
                    .build();
            cartIngreRepository.save(cartIngre);
        }
    }

    // 장바구니 조회용
    public PaymentResDto getCart(TokenUserInfo userInfo, Long userId) {

        List<CartCook> cartCookList = cartCookRepository.findByUserIdAndPaymentIdIsNull(userId);

        if (cartCookList.isEmpty()) {
            throw new CustomException("장바구니가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
        }

        List<PaymentItemResDto> items = cartCookList.stream().map(cartCook -> {
            CookDetailResDto cook = cookFeignClient.getCookDetail(cartCook.getCookId());
            List<IngredientResDto> defaultIngredients = cook.getIngredients();

            // 추가 식재료 조회
            List<CartIngredient> additionalIngredients = cartIngreRepository.findByCartCookId(cartCook.getId());
            List<IngredientResDto> additionalDtos = additionalIngredients.stream()
                    .filter(ingre -> "N".equals(ingre.getDefaultIngre()))
                    .map(ingre -> {
                        IngredientResDto ingreDetail = cookFeignClient.getIngredient(ingre.getIngreId());
                        return IngredientResDto.builder()
                                .ingredientId(ingre.getIngreId())
                                .ingreName(ingreDetail.getIngreName())
                                .unitQuantity(ingre.getUnitQuantity())
                                .unit(ingreDetail.getUnit())
                                .build();
                    }).toList();


            // 기본 + 추가식재료 합치기
            List<IngredientResDto> totalIngredients = new java.util.ArrayList<>();
            totalIngredients.addAll(defaultIngredients);
            totalIngredients.addAll(additionalDtos);

            return new PaymentItemResDto(
                    cook.getCookId(),
                    cook.getName(),
                    cook.getThumbnail(),
                    cartCook.getCount(),
                    totalIngredients
            );
        }).toList();

        return new PaymentResDto(cartCookList.get(0).getUserId(), items);
    }

}
