package com.sobok.paymentservice.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.querydsl.core.Tuple;
import com.sobok.paymentservice.payment.dto.cart.CartMonthlyHotDto;
import com.sobok.paymentservice.payment.entity.QPayment;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sobok.paymentservice.common.dto.TokenUserInfo;
import com.sobok.paymentservice.common.exception.CustomException;
import com.sobok.paymentservice.payment.client.CookFeignClient;
import com.sobok.paymentservice.payment.client.UserServiceClient;
import com.sobok.paymentservice.payment.dto.cart.CartAddCookReqDto;
import com.sobok.paymentservice.payment.dto.cart.CartStartPayDto;
import com.sobok.paymentservice.payment.dto.response.*;
import com.sobok.paymentservice.payment.entity.CartCook;
import com.sobok.paymentservice.payment.entity.CartIngredient;
import com.sobok.paymentservice.payment.entity.QCartCook;
import com.sobok.paymentservice.payment.repository.CartCookQueryRepository;
import com.sobok.paymentservice.payment.repository.CartCookRepository;
import com.sobok.paymentservice.payment.repository.CartIngreRepository;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.sobok.paymentservice.payment.entity.QCartCook.cartCook;
import static com.sobok.paymentservice.payment.entity.QPayment.payment;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CartService {
    private final CookFeignClient cookFeignClient;
    private final CartCookRepository cartCookRepository;
    private final CartIngreRepository cartIngreRepository;
    private final UserServiceClient userServiceClient;

    private final CartCookQueryRepository cartCookQueryRepository;

    private final ObjectMapper objectMapper;

    private final RedisTemplate<String, String> redisObjectTemplate;
    private final JPAQueryFactory queryFactory;

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
    public Long editCartCookCount(TokenUserInfo userInfo, Long cartCookId, Integer count, CartStartPayDto reqDto) {
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


        startPay(userInfo, reqDto);


        return cartCook.getId();
    }


    /**
     * 1. 장바구니에 담겨 있는지 확인
     * 2. Ingredient 다 지우기
     * 3. cart_cook 지우기
     *
     * @return
     */
    public Long deleteCart(TokenUserInfo userInfo, Long cookId, CartStartPayDto reqDto) {
        log.info("장바구니 삭제 서비스 로직 실행! id : {}", cookId);

        // 장바구니에 담겨 있는 지 확인
        CartCook cartCook = cartCookRepository.findUnpaidCartById(cookId).orElseThrow(
                () -> new CustomException("해당하는 장바구니의 요리가 없습니다.", HttpStatus.NOT_FOUND)
        );

        // 식재료 모두 삭제
        cartIngreRepository.deleteByCartCookId(cookId);

        // 요리 삭제
        cartCookRepository.delete(cartCook);

        startPay(userInfo, reqDto);

        return cartCook.getId();
    }

    // 장바구니 조회용
    public PaymentResDto getCart(TokenUserInfo userInfo, String status) {
        Long authId = userInfo.getId();
        List<CartCook> cartCookList;

        if ("cart".equals(status)) {
            // 유저 검증
            Boolean matched = userServiceClient.verifyUser(authId, userInfo.getUserId());
            if (!Boolean.TRUE.equals(matched)) {
                throw new CustomException("접근 불가", HttpStatus.FORBIDDEN);
            }
            cartCookList = cartCookRepository.findByUserIdAndPaymentIdIsNull(userInfo.getUserId());
        } else {
            cartCookList = cartCookRepository.findByPaymentId(Long.parseLong(status));
        }

        log.info("주문된 카트 목록 cartCookList : {}", cartCookList);

        if (cartCookList.isEmpty()) {
            throw new CustomException("장바구니가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
        }

        // cookId만 뽑아오기
        List<Long> cookIds = cartCookList.stream()
                .map(CartCook::getCookId)
                .distinct()
                .toList();

        // 요리 상세 정보 조회 (기본 재료 Id만 포함됨)
        Map<Long, CookDetailResDto> cookMap = cookFeignClient.getCookDetail(cookIds).stream()
                .collect(Collectors.toMap(CookDetailResDto::getCookId, Function.identity()));

        // 사용자 cartCookList에서 cartCookId만 뽑기
        List<Long> cartCookIds = cartCookList.stream()
                .map(CartCook::getId)
                .toList();

        // 장바구니 재료 전체 조회 (기본 + 추가)
        List<CartIngredient> cartIngredients = cartIngreRepository.findByCartCookIdIn(cartCookIds);

        // 기본 재료 ID, 추가 재료 ID 모두 모으기
        Set<Long> allIngreIds = new HashSet<>();
        // 요리의 기본 재료 ID 수집
        cookMap.values().forEach(cook -> allIngreIds.addAll(cook.getIngredientIds()));
        // 장바구니에서 추가된 재료 ID 수집
        allIngreIds.addAll(
                cartIngredients.stream()
                        .map(CartIngredient::getIngreId)
                        .collect(Collectors.toSet())
        );

        // 재료 상세 정보 한번에 조회
        Map<Long, IngredientResDto> ingreMap = cookFeignClient.getIngredients(new ArrayList<>(allIngreIds)).stream()
                .collect(Collectors.toMap(IngredientResDto::getIngredientId, Function.identity()));

        // cartCookId별 재료 목록 매핑
        Map<Long, List<CartIngredient>> cartCookIngreMap = cartIngredients.stream()
                .collect(Collectors.groupingBy(CartIngredient::getCartCookId));

        // PaymentItemResDto 생성 시 기본 재료, 추가 재료 ID -> 재료 맵에서 DTO 생성 후 결합
        List<PaymentItemResDto> items = cartCookList.stream().map(cartCook -> {
            CookDetailResDto cook = cookMap.get(cartCook.getCookId());

            // 기본 재료 매핑
            List<IngredientResDto> baseIngredients = cook.getIngredientIds().stream()
                    .map(ingreMap::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            // 추가 재료 매핑
            List<IngredientResDto> additionalIngredients = cartCookIngreMap.getOrDefault(cartCook.getId(), Collections.emptyList()).stream()
                    .filter(ingre -> "N".equals(ingre.getDefaultIngre()))
                    .map(ingre -> ingreMap.get(ingre.getIngreId()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            return PaymentItemResDto.builder()
                    .id(cartCook.getId())
                    .cookId(cook.getCookId())
                    .cookName(cook.getName())
                    .thumbnail(cook.getThumbnail())
                    .active(cook.getActive())
                    .quantity(cartCook.getCount())
                    .baseIngredients(baseIngredients)
                    .additionalIngredients(additionalIngredients)
                    .paymentId(cartCook.getPaymentId())
                    .build();
        }).toList();

        return new PaymentResDto(userInfo.getUserId(), items);
    }

    public void startPay(TokenUserInfo userInfo, CartStartPayDto reqDto) {
        try {
            String json = objectMapper.writeValueAsString(reqDto);
            redisObjectTemplate.opsForValue().set("START:PAYMENT:" + userInfo.getUserId(), json, Duration.ofMinutes(10));
        } catch (JsonProcessingException e) {
            log.error("DTO를 Redis로 파싱하는 과정에서 오류가 발생하였습니다.");
            throw new CustomException("Redis 파싱 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 한달 주문량 기준 요리 페이지 조회
     */
    public List<CookOrderCountDto> getPopularCookIds(int page, int size) {
        QCartCook cc = cartCook;
        QPayment p = payment;
        // 현재 시각 기준으로 한달 전 주문 조회
        long fromMillis = LocalDateTime.now().minusMonths(1)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        return queryFactory // cookId, 주문 수 형태로 조회
                .select(Projections.constructor(CookOrderCountDto.class,
                        cc.cookId,
                        cc.count()
                ))
                .from(cc)
                .join(p).on(cc.paymentId.eq(p.id)) // payment 테이블과 결제 ID 기준으로 조인
                .where(p.createdAt.goe(fromMillis))  // payment의 생성 시간이 한달 전 이후인 것만
                .groupBy(cc.cookId) // cookId 기준으로 그룹핑
                .orderBy(cc.count().desc()) // 내림차순
                .offset(PageRequest.of(page, size).getOffset()) // 페이징
                .limit(size)
                .fetch();
    }

//    public CartMonthlyHotDto getMonthlyHotList(int pageNo, int numOfRows) {
//        // 현재 시각 기준으로 한달 전 주문 조회
//        long monthToMillis = LocalDateTime.now().minusMonths(1)
//                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
//
//        // 페이징에 필요한 주문된 요리 최소 갯수
//        int pagingMinimum = pageNo * numOfRows;
//
//        // 주문량 순 요리 조회
//        List<CartMonthlyHotDto.MonthlyHot> monthlyHotList = cartCookQueryRepository.getMonthlyHotCartCook(monthToMillis).stream()
//                .map(t -> new CartMonthlyHotDto.MonthlyHot(t.get(cartCook.cookId), t.get(cartCook.count.sum().intValue())))
//                .toList();
//
//        // 페이징이 가능한지 확인
//        boolean isAvailable = monthlyHotList.size() >= pagingMinimum;
//
//        // 개수가 모자라다면 전체, 아니라면 개수에 맞게
//        List<CartMonthlyHotDto.MonthlyHot> result = isAvailable ?
//                monthlyHotList.subList(pagingMinimum - numOfRows, pagingMinimum) :
//                monthlyHotList;
//
//        return new CartMonthlyHotDto(result, isAvailable,  monthlyHotList.size());
//    }

}
