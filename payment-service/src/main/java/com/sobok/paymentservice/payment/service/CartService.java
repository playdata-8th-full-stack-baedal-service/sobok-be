package com.sobok.paymentservice.payment.service;

import com.sobok.paymentservice.common.dto.TokenUserInfo;
import com.sobok.paymentservice.common.exception.CustomException;
import com.sobok.paymentservice.payment.client.CookFeignClient;
import com.sobok.paymentservice.payment.client.UserServiceClient;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartService {
    private final CookFeignClient cookFeignClient;
    private final CartCookRepository cartCookRepository;
    private final CartIngreRepository cartIngreRepository;
    private final UserServiceClient userServiceClient;

    /**
     * 장바구니 추가
     * 1. cook service에서 요리를 꺼내와 요리 기본 식재료 가져오기
     * 2. cart_cook 데이터 저장
     * 3. cart_ingre 데이터 저장
     *
     * @return
     */
    @Transactional
    public Long addCartCook(TokenUserInfo userInfo, CartAddCookReqDto reqDto) {
        log.info("장바구니 추가 시작");

        // 수량이 0이면 예외 발생
        if (reqDto.getCount() <= 0) {
            log.error("잘못된 수량 입력이 발생하였습니다.");
            throw new CustomException("잘못된 수량 입력입니다", HttpStatus.BAD_REQUEST);
        }

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
                .userId(userInfo.getUserId())
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
                    .unitQuantity(defaultIngreList.get(key) * reqDto.getCount())
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

        return cartCook.getId();
    }

    /**
     * 장바구니 수정
     * 1. 수량 검증
     * 2. 상품 조회
     * 3. 수량 변경
     *
     * @return
     */
    public Long editCartCookCount(Long cartCookId, Integer count) {
        log.info("장바구니 수정 서비스 로직 시작! cook id : {}, count : {}", cartCookId, count);

        // 수량 검증
        if (count <= 0) {
            log.error("잘못된 수량 요청값이 들어왔습니다.");
            throw new CustomException("잘못된 수량 입력입니다.", HttpStatus.BAD_REQUEST);
        }

        // 장바구니 상품 꺼내오기
        CartCook cartCook = cartCookRepository.findUnpaidCartById(cartCookId).orElseThrow(
                () -> new CustomException("해당하는 장바구니의 요리가 없습니다.", HttpStatus.NOT_FOUND)
        );

        // 수량 변경
        cartCook.changeCount(count);

        // 저장
        cartCookRepository.save(cartCook);

        return cartCook.getId();
    }


    /**
     * 1. 장바구니에 담겨 있는지 확인
     * 2. Ingredient 다 지우기
     * 3. cart_cook 지우기
     *
     * @return
     */
    public Long deleteCart(Long cookId) {
        log.info("장바구니 삭제 서비스 로직 실행! id : {}", cookId);

        // 장바구니에 담겨 있는 지 확인
        CartCook cartCook = cartCookRepository.findUnpaidCartById(cookId).orElseThrow(
                () -> new CustomException("해당하는 장바구니의 요리가 없습니다.", HttpStatus.NOT_FOUND)
        );

        // 식재료 모두 삭제
        cartIngreRepository.deleteByCartCookId(cookId);

        // 요리 삭제
        cartCookRepository.delete(cartCook);

        return cartCook.getId();
    }

    // 장바구니 조회용
    public PaymentResDto getCart(TokenUserInfo userInfo) {
        Long authId = userInfo.getId();

        // 유저 검증
        Boolean matched = userServiceClient.verifyUser(authId, userInfo.getUserId());
        if (!Boolean.TRUE.equals(matched)) {
            throw new CustomException("접근 불가", HttpStatus.FORBIDDEN);
        }

        List<CartCook> cartCookList = cartCookRepository.findByUserIdAndPaymentIdIsNull(userInfo.getUserId());

        if (cartCookList.isEmpty()) {
            throw new CustomException("장바구니가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
        }

        List<PaymentItemResDto> items = cartCookList.stream().map(cartCook -> {
            CookDetailResDto cook = cookFeignClient.getCookDetail(cartCook.getCookId());
            List<IngredientResDto> baseIngredients = cook.getIngredients(); // 기본 식재료

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
                                .price(ingreDetail.getPrice())
                                .build();
                    }).toList();

            // 기본/추가 식재료 분리해서 넣기)
            return PaymentItemResDto.builder()
                    .cookId(cook.getCookId())
                    .cookName(cook.getName())
                    .thumbnail(cook.getThumbnail())
                    .quantity(cartCook.getCount())
                    .baseIngredients(baseIngredients) // 기본 식재료
                    .additionalIngredients(additionalDtos) // 추가 식재료
                    .build();
        }).toList();

        return new PaymentResDto(userInfo.getUserId(), items);
    }


}
