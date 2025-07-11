package com.sobok.paymentservice.payment.service;

import com.sobok.paymentservice.common.dto.TokenUserInfo;
import com.sobok.paymentservice.common.enums.OrderState;
import com.sobok.paymentservice.common.exception.CustomException;
import com.sobok.paymentservice.payment.client.CookFeignClient;
import com.sobok.paymentservice.payment.client.DeliveryFeignClient;
import com.sobok.paymentservice.payment.dto.delivery.DeliveryResDto;
import com.sobok.paymentservice.payment.dto.payment.AdminPaymentResDto;

import com.sobok.paymentservice.payment.client.ShopFeignClient;
import com.sobok.paymentservice.payment.client.UserServiceClient;
import com.sobok.paymentservice.payment.dto.payment.*;
import com.sobok.paymentservice.payment.dto.response.*;
import com.sobok.paymentservice.payment.dto.payment.PaymentRegisterReqDto;
import com.sobok.paymentservice.payment.dto.payment.ShopAssignDto;
import com.sobok.paymentservice.payment.dto.payment.TossPayRegisterReqDto;
import com.sobok.paymentservice.payment.dto.shop.AdminShopResDto;
import com.sobok.paymentservice.payment.dto.shop.ShopPaymentResDto;
import com.sobok.paymentservice.payment.dto.user.UserInfoResDto;
import com.sobok.paymentservice.payment.entity.CartCook;
import com.sobok.paymentservice.payment.entity.Payment;
import com.sobok.paymentservice.payment.repository.CartCookRepository;
import com.sobok.paymentservice.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.sobok.paymentservice.payment.dto.response.CartCookResDto;
import com.sobok.paymentservice.payment.dto.response.CartIngredientResDto;

import com.sobok.paymentservice.payment.repository.CartIngreRepository;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final CartCookRepository cartCookRepository;
    private final ShopFeignClient shopFeignClient;
    private final UserServiceClient userServiceClient;
    private final CookFeignClient cookFeignClient;
    private final CartService cartService;
    private final CartIngreRepository CartIngreRepository;
    private final DeliveryFeignClient deliveryFeignClient;

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
        if (cartCookList.isEmpty()) {
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
    public PagedResponse<AdminPaymentResDto> getAllPaymentsForAdmin(Pageable pageable) {
        Page<Payment> payments = paymentRepository.findAllByOrderByCreatedAtDesc(pageable);

        List<AdminPaymentResDto> content = payments.getContent().stream()
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

        return PagedResponse.<AdminPaymentResDto>builder()
                .content(content)
                .page(payments.getNumber())
                .size(payments.getSize())
                .totalPages(payments.getTotalPages())
                .totalElements(payments.getTotalElements())
                .first(payments.isFirst())
                .last(payments.isLast())
                .build();
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


    public List<GetPaymentResDto> getPayment(TokenUserInfo userInfo, Long pageNo, Long numOfRows) {
        // 유저 검증
        Boolean matched = userServiceClient.verifyUser(userInfo.getId(), userInfo.getUserId());
        if (!Boolean.TRUE.equals(matched)) {
            throw new CustomException("접근 불가", HttpStatus.FORBIDDEN);
        }

        //일단 userId로 cart_cook에서 조회
        List<CartCook> cartCookList = cartCookRepository.findByUserId((userInfo.getUserId()));
        log.info("userId로 찾아온 cartCookList : {}", cartCookList);
        // 결제된 항목만 필터링
        List<CartCook> orderedCartCooks = cartCookList.stream()
                .filter(cart -> cart.getPaymentId() != null)
                .toList();

        List<Long> cookIdList = orderedCartCooks.stream().map(CartCook::getCookId).distinct().toList(); //중복제거
        List<Long> paymentIdList = cartCookList.stream().map(CartCook::getPaymentId).distinct().toList();

        //cook-service로 요청보내서 요리 이름, 요리 이미지 주소 얻어오기
        List<CookDetailResDto> cookDetails = cookFeignClient.getCookDetails(cookIdList);
        log.info("주문한 요리 정보 cookDetails : {}", cookDetails);

        //결제 정보 가져오기
        Pageable pageable = PageRequest.of(pageNo.intValue() - 1, numOfRows.intValue(), Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Payment> pagedPayments = paymentRepository.findByIdIn(paymentIdList, pageable);
        List<Payment> payments = pagedPayments.getContent();
        log.info("찾아온 payment: {}", payments);

        // 근데 여러개의 cart_cook이 하나의 paymentId를 가질 수 있음
        //CartCook에서 cookId를 가지고 바로 요리정보 찾아오기 위해 맵으로 매핑
        Map<Long, CookDetailResDto> cookMap = cookDetails.stream()
                .collect(Collectors.toMap(CookDetailResDto::getCookId, Function.identity()));
        //paymentId 기준으로 CartCook 묶기 (주문 단위로 그룹핑)
        Map<Long, List<CartCook>> cartCookByPayment = orderedCartCooks.stream()
                .collect(Collectors.groupingBy(CartCook::getPaymentId));

        // cartCook 기반으로 Dto 생성
        List<GetPaymentResDto> paymentDtos = payments.stream()
                .map(payment -> {
                    List<CartCook> cartCooks = cartCookByPayment.getOrDefault(payment.getId(), Collections.emptyList());

                    List<GetPaymentResDto.Cook> cookDtos = cartCooks.stream()
                            .map(cart -> {
                                CookDetailResDto cookDetail = cookMap.get(cart.getCookId());
                                if (cookDetail == null) {
                                    throw new CustomException("요리 정보가 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
                                }
                                return GetPaymentResDto.Cook.builder()
                                        .cookName(cookDetail.getName())
                                        .thumbnail(cookDetail.getThumbnail())
                                        .build();
                            })
                            .toList();

                    return GetPaymentResDto.builder()
                            .paymentId(payment.getId())
                            .orderId(payment.getOrderId())
                            .orderState(payment.getOrderState())
                            .totalPrice(payment.getTotalPrice())
                            .createdAt(payment.getCreatedAt())
                            .cook(cookDtos)
                            .build();
                })
                .toList();


        //응답: payment - 주문 번호(orderId), 주문 일자, 배송 상태, 결제 금액. cart_cook - 주문한 요리 사진과 이름
        //정렬 및 페이징 처리
        log.info("응답할 dto: {}", paymentDtos);
        return paymentDtos;
    }

    public PaymentDetailResDto getPaymentDetail(TokenUserInfo userInfo, Long paymentId) {
        // paymentId로 Cart Cook 리스트 가져오기
        List<CartCook> cartCookList = cartCookRepository.findByPaymentId(paymentId);
        if (cartCookList.isEmpty()) {
            log.error("결제 내역에 해당하는 카트 정보가 존재하지 않습니다. | payment id : {}", paymentId);
            throw new CustomException("결제 내역에 해당하는 카트 정보가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        log.info("userInfo 전체 확인: {}", userInfo);

        if ("USER".equals(userInfo.getRole())) {
            //유저 검증
            Boolean matched = userServiceClient.verifyUser(userInfo.getId(), userInfo.getUserId());
            if (!Boolean.TRUE.equals(matched)) {
                throw new CustomException("접근 불가", HttpStatus.FORBIDDEN);
            }
            // 로그인한 사용자 본인이 주문한게 맞는지 확인
            cartCookList.forEach(cart -> {
                if (!cart.getUserId().equals(userInfo.getUserId())) {
                    throw new CustomException("주문한 사용자만 조회가능합니다.", HttpStatus.FORBIDDEN);
                }
            });
        }

        // paymentId로 payment 정보 조회
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(
                () -> new CustomException("주문 정보가 존재하지 않습니다.", HttpStatus.BAD_REQUEST)
        );

        // 주문한 요리 식재료, 추가 식재료 정보 얻기
        PaymentResDto paymentResDto = cartService.getCart(userInfo, paymentId.toString());
        List<PaymentItemResDto> items = paymentResDto.getItems().stream()
                .filter(item -> item.getPaymentId().equals(paymentId))
                .toList();
        log.info("items: {}", items);

        // shopId를 얻기 위해 delivery-service에 요청
        DeliveryResDto delivery = deliveryFeignClient.getDelivery(paymentId);

        if ("HUB".equals(userInfo.getRole())) {
            //가게 검증
            if (!Objects.equals(delivery.getShopId(), userInfo.getShopId())) {
                throw new CustomException("현재 사용자(authId:" + userInfo.getId() + ", shopId:" + userInfo.getShopId()
                        + ")는 해당 주문(paymentId=" + paymentId + ")에 접근할 수 없습니다.", HttpStatus.FORBIDDEN);
            }
        }

        // 배송지
        UserInfoResDto userInfoResDto = userServiceClient.getUserInfo(payment.getUserAddressId());
        log.info("사용자 주소 정보를 얻기 위한 userInfoResDto: {}", userInfoResDto);


        // shopId로 shop 정보 얻기
        AdminShopResDto shopInfo = shopFeignClient.getShopInfo(delivery.getShopId());


        // payment: 주문 번호, 일자, 배송 상태, 결제 수단 및 총금액, 주소(id), 라이더 요청사항
        // cook: 포함된 모든 요리 정보, 추가 식재료,
        // 배송지 -> user_address_id 가지고 user-service로 가서 road_full과 addr_detail 가져와야함.
        // shop 정보: 이름, 주소
        return PaymentDetailResDto.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .orderState(payment.getOrderState())
                .totalPrice(payment.getTotalPrice())
                .createdAt(payment.getCreatedAt())
                .payMethod(payment.getPayMethod())
                .riderRequest(payment.getRiderRequest())
                .roadFull(userInfoResDto.getRoadFull())
                .addrDetail(userInfoResDto.getAddress())
                .shopName(shopInfo.getShopName())
                .shopAddress(shopInfo.getShopAddress())
                .completeTime(delivery.getCompleteTime())
                .items(items)
                .build();

    }

    public List<ShopPaymentResDto> getPaymentList(List<Long> ids) {
        List<Payment> paymentList = paymentRepository.findAllById(ids);
        return paymentList.stream()
                .map(payment -> ShopPaymentResDto.builder()
                        .paymentId(payment.getId())
                        .orderId(payment.getOrderId())
                        .orderState(payment.getOrderState())
                        .createdAt(payment.getCreatedAt())
                        .build())
                .toList();
    }

    public String resetPayment(String orderId) {
        Payment payment = paymentRepository.getPendingPaymentByOrderId(orderId, OrderState.ORDER_PENDING).orElseThrow(
                () -> new CustomException("해당하는 결제 정보가 없습니다.", HttpStatus.BAD_REQUEST)
        );

        List<CartCook> cartCookList = cartCookRepository.findByPaymentId(payment.getId());
        for (CartCook cartCook : cartCookList) {
            cartCook.detachFromPayment();
            cartCookRepository.save(cartCook);
        }

        return orderId;
    }

    public void changeOrderState(TokenUserInfo userInfo, ChangeOrderStateReqDto changeOrderState) {
        DeliveryResDto delivery = deliveryFeignClient.getDelivery(changeOrderState.getPaymentId());

        //가게 검증
        if ("HUB".equals(userInfo.getRole())) {
            if (!Objects.equals(delivery.getShopId(), userInfo.getShopId())) {
                throw new CustomException("현재 사용자(authId:" + userInfo.getId() + ", shopId:" + userInfo.getShopId()
                        + ")는 해당 주문(paymentId=" + changeOrderState.getPaymentId() + ")에 접근할 수 없습니다.", HttpStatus.FORBIDDEN);
            }
        }
        //라이더 검증
        if ("RIDER".equals(userInfo.getRole())) {
            if (!Objects.equals(delivery.getRiderId(), userInfo.getRiderId())) {
                throw new CustomException("현재 사용자(authId:" + userInfo.getId() + ", riderId:" + userInfo.getRiderId()
                        + ")는 해당 주문(paymentId=" + changeOrderState.getPaymentId() + ")에 접근할 수 없습니다.", HttpStatus.FORBIDDEN);
            }
        }

        //해당 payment 주문 상태 가져오기
        Payment payment = paymentRepository.findById(changeOrderState.getPaymentId()).orElseThrow(
                () -> new CustomException("해당 주문 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND)
        );

        payment.nextState();
        paymentRepository.save(payment);
        log.info("해당 주문 상태가 변경되었습니다.");
    }
}
