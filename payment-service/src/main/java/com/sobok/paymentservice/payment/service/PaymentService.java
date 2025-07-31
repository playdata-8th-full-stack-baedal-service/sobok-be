package com.sobok.paymentservice.payment.service;

import com.querydsl.core.BooleanBuilder;
import com.sobok.paymentservice.common.dto.TokenUserInfo;
import com.sobok.paymentservice.common.enums.DeliveryState;
import com.sobok.paymentservice.common.enums.OrderState;
import com.sobok.paymentservice.common.exception.CustomException;
import com.sobok.paymentservice.payment.client.*;
import com.sobok.paymentservice.payment.dto.delivery.AcceptOrderReqDto;
import com.sobok.paymentservice.payment.dto.delivery.DeliveryResDto;
import com.sobok.paymentservice.payment.dto.payment.AdminPaymentResDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sobok.paymentservice.payment.dto.payment.*;
import com.sobok.paymentservice.payment.dto.response.*;
import com.sobok.paymentservice.payment.dto.payment.PaymentRegisterReqDto;
import com.sobok.paymentservice.payment.dto.payment.ShopAssignDto;
import com.sobok.paymentservice.payment.dto.payment.TossPayRegisterReqDto;
import com.sobok.paymentservice.payment.dto.shop.AdminShopResDto;
import com.sobok.paymentservice.payment.dto.shop.ShopPaymentResDto;
import com.sobok.paymentservice.payment.dto.user.UserInfoResDto;
import com.sobok.paymentservice.payment.entity.CartCook;
import com.sobok.paymentservice.payment.entity.CartIngredient;
import com.sobok.paymentservice.payment.entity.Payment;
import com.sobok.paymentservice.payment.entity.QPayment;
import com.sobok.paymentservice.payment.repository.CartCookRepository;
import com.sobok.paymentservice.payment.repository.PaymentRepository;
import com.sobok.paymentservice.payment.service.validator.access.RoleAccessValidator;
import com.sobok.paymentservice.payment.service.validator.deliveryAction.DeliveryActionHandler;
import com.sobok.paymentservice.payment.service.validator.deliveryAction.DeliveryActionStrategyFactory;
import com.sobok.paymentservice.payment.service.validator.orderstate.RoleValidator;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Consumer;
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
    private final JPAQueryFactory factory;
    private final Map<String, RoleValidator> validatorMap;
    private final List<RoleAccessValidator> roleAccessValidatorList;
    private final DeliveryActionStrategyFactory strategyFactory;
    private final AuthFeignClient authFeignClient;

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

        // 요리 결제 정보 등록 TODO (N + 1)
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

        // cartIngreDient 가져오기
        List<CartCook> cartCookList = cartCookRepository.findByPaymentId(payment.getId());
        List<PaymentItemResDto> paymentResDto = cartService.getPaymentResDto(null, cartCookList).getItems();
        List<StockReqDto> ingreList = new ArrayList<>();
        for (PaymentItemResDto cook : paymentResDto) {
            cook.getBaseIngredients()
                    .stream()
                    .map(
                            ingredient ->
                                    new StockReqDto(
                                            -1L,
                                            ingredient.getIngredientId(),
                                            ingredient.getUnit() * ingredient.getUnitQuantity()
                                    )
                    ).forEach(ingreList::add);
            cook.getAdditionalIngredients()
                    .stream()
                    .map(
                            ingredient ->
                                    new StockReqDto(
                                            -1L,
                                            ingredient.getIngredientId(),
                                            ingredient.getUnit() * ingredient.getUnitQuantity()
                                    )
                    ).forEach(ingreList::add);
        }

        Map<Long, Integer> ingreMap = new HashMap<>();
        for (StockReqDto dto : ingreList) {
            if (ingreMap.containsKey(dto.getIngredientId())) {
                Integer quantity = ingreMap.get(dto.getIngredientId()) + dto.getQuantity();
                ingreMap.put(dto.getIngredientId(), quantity);
            } else {
                ingreMap.put(dto.getIngredientId(), dto.getQuantity());
            }
        }

        // 가게 자동 배정 시작
        shopFeignClient.assignNearestShop(new ShopAssignDto(payment.getUserAddressId(), payment.getId(), ingreMap));

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
        log.info(payments.toString());

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
        log.info("<UNK> <UNK> <UNK> <UNK> <UNK> | <UNK> : {}", content);

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
     * 결제 정보에 맞는 요리 이름 조회용
     */
    public List<Long> getCookIdsByPaymentId(Long paymentId) {
        List<Long> cookIds = cartCookRepository.findByPaymentId(paymentId)
                .stream()
                .map(CartCook::getCookId)
                .distinct()
                .toList();

        if (cookIds.isEmpty()) {
            throw new CustomException("존재하지 않는 주문입니다.", HttpStatus.NOT_FOUND);
        }
        return cookIds;
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

    /**
     * 사용자 주문 전체 조회
     */
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

        if (orderedCartCooks.isEmpty()) return List.of();

        List<Long> cookIdList = orderedCartCooks.stream().map(CartCook::getCookId).distinct().toList(); //중복제거
        List<Long> paymentIdList = cartCookList.stream().map(CartCook::getPaymentId).distinct().toList();

        //cook-service로 요청보내서 요리 이름, 요리 이미지 주소 얻어오기
        ResponseEntity<List<CookInfoResDto>> cookDetails = cookFeignClient.getCooksInfo(cookIdList);
        if (cookDetails.getBody() == null || cookDetails.getBody().isEmpty()) {
            throw new CustomException("요리 정보를 불러오지 못했습니다.", HttpStatus.BAD_REQUEST);
        }
        log.info("주문한 요리 정보 cookDetails : {}", cookDetails);

        //결제 정보 가져오기
        Pageable pageable = PageRequest.of(pageNo.intValue() - 1, numOfRows.intValue(), Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Payment> pagedPayments = paymentRepository.findByIdIn(paymentIdList, pageable);
        List<Payment> payments = pagedPayments.getContent();
        log.info("찾아온 payment: {}", payments);

        // 근데 여러개의 cart_cook이 하나의 paymentId를 가질 수 있음
        //CartCook에서 cookId를 가지고 바로 요리정보 찾아오기 위해 맵으로 매핑
        Map<Long, CookInfoResDto> cookMap = cookDetails.getBody().stream()
                .collect(Collectors.toMap(CookInfoResDto::getCookId, Function.identity()));
        //paymentId 기준으로 CartCook 묶기 (주문 단위로 그룹핑)
        Map<Long, List<CartCook>> cartCookByPayment = orderedCartCooks.stream()
                .collect(Collectors.groupingBy(CartCook::getPaymentId));

        // cartCook 기반으로 Dto 생성
        List<GetPaymentResDto> paymentDtos = payments.stream()
                .map(payment -> {
                    List<CartCook> cartCooks = cartCookByPayment.getOrDefault(payment.getId(), Collections.emptyList());

                    List<GetPaymentResDto.Cook> cookDtos = (cartCooks != null ? cartCooks : List.<CartCook>of())
                            .stream()
                            .map(cart -> {
                                CookInfoResDto cookDetail = cookMap.get(cart.getCookId());
                                if (cookDetail == null)
                                    throw new CustomException("요리 정보가 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
                                return GetPaymentResDto.Cook.builder()
                                        .cookId(cookDetail.getCookId())
                                        .cookName(cookDetail.getName())
                                        .thumbnail(cookDetail.getThumbnail())
                                        .build();
                            }).toList();

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

    /**
     * 사용자/가게 주문 세부 조회
     */
    public PaymentDetailResDto getPaymentDetail(TokenUserInfo userInfo, Long paymentId) {
        // paymentId로 payment 정보 조회
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(
                () -> new CustomException("주문 정보가 존재하지 않습니다.", HttpStatus.BAD_REQUEST)
        );

        // paymentId로 Cart Cook 리스트 가져오기
        List<CartCook> cartCookList = cartCookRepository.findByPaymentId(paymentId);
        if (cartCookList.isEmpty()) {
            log.error("결제 내역에 해당하는 카트 정보가 존재하지 않습니다. | payment id : {}", paymentId);
            throw new CustomException("결제 내역에 해당하는 카트 정보가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        // shopId를 얻기 위해 delivery-service에 요청
        ResponseEntity<DeliveryResDto> response = deliveryFeignClient.getDelivery(paymentId);
        if (response.getBody() == null) {
            throw new CustomException("배달 정보를 불러오지 못했습니다.", HttpStatus.BAD_REQUEST);
        }
        DeliveryResDto delivery = response.getBody();

        RoleAccessValidator validator = roleAccessValidatorList.stream()
                .filter(v -> Objects.equals(v.getRole(), userInfo.getRole()))
                .findFirst()
                .orElseThrow(() -> new CustomException("해당 역할(" + userInfo.getRole() + ")은 접근 권한이 없습니다.", HttpStatus.BAD_REQUEST));

        validator.validate(userInfo, cartCookList, delivery);

        // shopId로 shop 정보 얻기
        AdminShopResDto shopInfo = shopFeignClient.getShopInfo(delivery.getShopId()).getBody();

        // 주문한 요리 식재료, 추가 식재료 정보 얻기
        List<CartCook> byPaymentId = cartCookRepository.findByPaymentId(paymentId);
        PaymentResDto paymentResDto = cartService.getPaymentResDto(userInfo.getUserId(), byPaymentId);
        List<PaymentItemResDto> items = paymentResDto.getItems().stream()
                .filter(item -> item.getPaymentId().equals(paymentId))
                .toList();
        log.info("items: {}", items);

        // 배송지
        UserInfoResDto userInfoResDto = userServiceClient.getUserInfo(payment.getUserAddressId()).getBody();
        log.info("사용자 주소 정보를 얻기 위한 userInfoResDto: {}", userInfoResDto);

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

    /**
     * 가게에 들어온 전체 주문 조회용 paymentId로 주문 정보 받기
     */
    public List<ShopPaymentResDto> getPaymentList(List<Long> ids) {
        List<Payment> paymentList = paymentRepository.findAllById(ids);
        return paymentList.stream()
                .map(payment -> ShopPaymentResDto.builder()
                        .paymentId(payment.getId())
                        .orderId(payment.getOrderId())
                        .orderState(payment.getOrderState())
                        .updatedAt(payment.getUpdatedAt())
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
        }

        cartCookRepository.saveAll(cartCookList);

        paymentRepository.delete(payment);

        return orderId;
    }

    /**
     * 배달 조회에 사용되는 paymentId로 주문 정보 받기
     */
    public List<ShopPaymentResDto> getRiderAvailPaymentList(List<Long> ids, @Nullable List<OrderState> filterStates) {
        QPayment payment = QPayment.payment;

        BooleanBuilder builder = new BooleanBuilder()
                .and(payment.id.in(ids));

        if (filterStates != null && !filterStates.isEmpty()) {
            builder.and(payment.orderState.in(filterStates));
        }

        List<Payment> paymentList = factory
                .selectFrom(payment)
                .where(builder)
                .orderBy(payment.updatedAt.asc())
                .fetch();

        log.info("paymentList: {}", paymentList);

        return paymentList.stream()
                .map(p -> ShopPaymentResDto.builder()
                        .paymentId(p.getId())
                        .orderId(p.getOrderId())
                        .orderState(p.getOrderState())
                        .userAddressId(p.getUserAddressId())
                        .updatedAt(p.getUpdatedAt())
                        .build())
                .toList();
    }

    /**
     * 주문 상태 변경 (재료 준비 -> 준비 완료 / 배달 승인 -> 배달 중)
     */
    @Transactional
    public void checkUserInfo(TokenUserInfo userInfo, Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(
                () -> new CustomException("해당 주문 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND)
        );
        ResponseEntity<DeliveryResDto> response = deliveryFeignClient.getDelivery(paymentId);
        if (response.getBody() == null) {
            throw new CustomException("배달 정보를 불러오지 못했습니다.", HttpStatus.BAD_REQUEST);
        }
        DeliveryResDto delivery = response.getBody();

        // 역할에 맞는 validator 찾기
        RoleValidator validator = validatorMap.get(userInfo.getRole());

        if (validator == null) {
            throw new CustomException("지원하지 않는 역할입니다: " + userInfo.getRole(), HttpStatus.FORBIDDEN);
        }

        // 권한 체크
        validator.validate(userInfo, delivery);

        // 상태 체크
        if (!validator.allowedStates().contains(payment.getOrderState())) {
            throw new CustomException("현재 상태에서는 상태 변경이 허용되지 않습니다. (현재 상태: " + payment.getOrderState() + ")",
                    HttpStatus.BAD_REQUEST);
        }

        //해당 payment 주문 상태 변경
        payment.nextState();
        paymentRepository.save(payment);
        log.info("해당 주문 상태가 변경되었습니다.");
    }

    /**
     * 결제 상태 정보
     */
    public Boolean isPaymentCompleted(Long paymentId, Long userId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new CustomException("결제가 존재하지 않습니다.", HttpStatus.NOT_FOUND));

        Long userAddressId = payment.getUserAddressId();
        Long ownerUserId = userServiceClient.getUserIdByUserAddressId(userAddressId).getBody();

        if (!Objects.requireNonNull(ownerUserId).equals(userId)) {
            throw new CustomException("결제에 대한 접근 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        return payment.getOrderState() == OrderState.DELIVERY_COMPLETE;
    }

    /**
     * paymentId에 해당하는 CartCook의 ID를 반환
     */
    public Long getCartCookIdByPaymentId(Long paymentId) {
        return cartCookRepository.findByPaymentId(paymentId).stream()
                .findFirst()
                .orElseThrow(() -> new CustomException("결제 ID에 해당하는 CartCook이 없습니다. id=" + paymentId, HttpStatus.NOT_FOUND))
                .getId();
    }

    /**
     * 요리 ID로 기본 식재료 목록 조회
     */
    public List<IngredientTwoResDto> getDefaultIngredients(Long cookId) {
        return cookFeignClient.getBaseIngredients(cookId).getBody();
    }

    /**
     * cartCookId로 추가 식재료 목록 조회
     */
    public List<IngredientTwoResDto> getExtraIngredients(Long cartCookId) {
        List<CartIngredient> ingredients = CartIngreRepository.findByCartCookIdAndDefaultIngre(cartCookId, "N");

        if (ingredients.isEmpty()) {
//                throw new CustomException("cartCookId " + cartCookId + "번의 추가 식재료가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
            log.info("cartCookId " + cartCookId + "번의 추가 식재료가 존재하지 않습니다.");
            return List.of();
        }

        return ingredients.stream().map(cartIngre -> {
            IngredientTwoResDto info = cookFeignClient.getIngredientInfo(cartIngre.getIngreId()).getBody();
            if (info == null) {
                throw new CustomException("추가 식재료 정보를 가져오지 못했습니다. id=" + cartIngre.getIngreId(), HttpStatus.NOT_FOUND);
            }
            info.setQuantity(cartIngre.getUnitQuantity());
            info.setDefaultFlag(false);
            return info;
        }).toList();
    }

    /**
     * 요리 ID로 요리 이름을 조회
     */
    public String getCookName(Long cookId) {
        return cookFeignClient.getCookNameById(cookId).getBody();
    }

    /**
     * 라이더용 배달 승인/완료
     */
    @Transactional
    public void processDeliveryAction(
            TokenUserInfo userInfo, Long paymentId, DeliveryState state, Consumer<AcceptOrderReqDto> deliveryAction
    ) {
        Payment payment = getAndValidatePayment(paymentId, state);
        DeliveryActionHandler strategy = strategyFactory.getStrategy(state);
        strategy.execute(userInfo, paymentId, deliveryAction, payment);
    }

    private Payment getAndValidatePayment(Long paymentId, DeliveryState state) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() ->
                new CustomException("해당 주문 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        Map<DeliveryState, OrderState> validStates = Map.of(
                DeliveryState.ASSIGN, OrderState.READY_FOR_DELIVERY,
                DeliveryState.COMPLETE, OrderState.DELIVERING
        );

        if (payment.getOrderState() != validStates.get(state)) {
            throw new CustomException(
                    validStates.get(state) + " 상태에서만 " + state + " 작업이 가능합니다.",
                    HttpStatus.BAD_REQUEST
            );
        }
        return payment;
    }

    /**
     * 관리자 전용 사용자 주문 전체 조회
     *
     * @return
     */
    public List<AdminPaymentBasicResDto> getAllPayments(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")); // 최신순 정렬

        Page<Payment> payments = paymentRepository.findAllByOrderByCreatedAtDesc(pageable);
        log.info("page로 조회한 payments: {}", payments);

        return payments.getContent().stream()
                .map(payment -> AdminPaymentBasicResDto.builder()
                        .paymentId(payment.getId())
                        .orderId(payment.getOrderId())
                        .createdAt(payment.getCreatedAt())
                        .build())
                .toList();
    }

    public AdminPaymentResponseDto getPaymentDetail(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() ->
                new CustomException("결제 정보를 찾지 못하였습니다.", HttpStatus.NOT_FOUND)
        );

        // 사용자 정보
        UserInfoResDto userInfoResDto = userServiceClient.getUserInfo(payment.getUserAddressId()).getBody();
        String loginId = authFeignClient.getLoginId(userInfoResDto.getAuthId()).getBody();

        // 배달, 라이더 정보
        RiderPaymentInfoResDto riderPaymentInfoResDto = deliveryFeignClient.getDeliveryAndRider(paymentId).getBody();

        // 가게 정보
        AdminShopResDto shopInfo = shopFeignClient.getShopInfo(Objects.requireNonNull(riderPaymentInfoResDto).getShopId()).getBody();

        // 요리 + 기본식재료 + 추가식재료
        List<CookDetailWithIngredientsResDto> cooks = getCookDetailsByPaymentId(paymentId);

        return AdminPaymentResponseDto.builder()
                .orderId(payment.getOrderId())
                .totalPrice(payment.getTotalPrice())
                .payMethod(payment.getPayMethod())
                .orderState(payment.getOrderState())
                .createdAt(payment.getCreatedAt())
                .completeTime(riderPaymentInfoResDto.getCompleteTime())
                .nickname(userInfoResDto.getNickname())
                .phone(userInfoResDto.getPhone())
                .roadFull(userInfoResDto.getRoadFull())
                .address(userInfoResDto.getAddress())
                .riderName(riderPaymentInfoResDto.getRiderName())
                .riderPhone(riderPaymentInfoResDto.getRiderPhone())
                .shopName(shopInfo.getShopName())
                .shopPhone(shopInfo.getShopPhone())
                .ownerName(shopInfo.getOwnerName())
                .shopAddress(shopInfo.getShopAddress())
                .cooks(cooks)
                .loginId(loginId)
                .build();
    }

    /**
     * 결제 ID에 해당하는 장바구니 요리이름 재료 정보를 조회,
     * 요리 이름과 기본/추가 식재료 목록을 포함한 상세 DTO 리스트를 반환
     *
     * @param paymentId 결제 ID
     * @return List<CookDetailWithIngredientsResDto> 요리 이름, 기본 재료, 추가 재료 포함된 DTO 목록
     */
    public List<CookDetailWithIngredientsResDto> getCookDetailsByPaymentId(Long paymentId) {
        // 결제 ID에 해당하는 모든 장바구니 요리 목록 조회
        List<CartCookResDto> cartCooks = getCartCooksByPaymentId(paymentId);

        // 요리 ID 목록만 추출
        List<Long> cookIds = cartCooks.stream()
                .map(CartCookResDto::getCookId)
                .distinct()
                .toList();

        // 요리 ID -> 요리 이름 Map으로 변환 (cookId 기준)
        ResponseEntity<List<CookNameResDto>> cookNames = cookFeignClient.getCookNames(cookIds);
        if (cookNames.getBody() == null || cookNames.getBody().isEmpty()) {
            throw new CustomException("요리 정보를 불러오지 못했습니다.", HttpStatus.BAD_REQUEST);
        }
        Map<Long, String> cookNameMap = cookNames.getBody().stream()
                .collect(Collectors.toMap(CookNameResDto::getCookId, CookNameResDto::getCookName));

        List<CookDetailWithIngredientsResDto> result = new ArrayList<>();

        // 각 장바구니 요리별로 재료 조회 및 가공
        for (CartCookResDto cartCook : cartCooks) {
            // 해당 장바구니 요리에 포함된 재료 목록 조회
            List<CartIngredientResDto> ingredients = getIngredientsByCartCookId(cartCook.getId());

            // 기본 재료 ID만 필터링
            List<Long> baseIds = ingredients.stream()
                    .filter(i -> "Y".equals(i.getDefaultIngre()))
                    .map(CartIngredientResDto::getIngreId)
                    .toList();
            // 추가 재료 ID만 필터링
            List<Long> addIds = ingredients.stream()
                    .filter(i -> "N".equals(i.getDefaultIngre()))
                    .map(CartIngredientResDto::getIngreId)
                    .toList();
            //기본 재료 ID 이름 변환
            ResponseEntity<List<IngredientNameResDto>> baseIngredientNames = cookFeignClient.getIngredientNames(baseIds);
            if (baseIngredientNames.getBody() == null || baseIngredientNames.getBody().isEmpty()) {
                throw new CustomException("기본 식재료 정보를 불러오지 못했습니다.", HttpStatus.BAD_REQUEST);
            }
            List<String> baseNames = baseIngredientNames.getBody().stream()
                    .map(IngredientNameResDto::getIngreName)
                    .toList();

            List<String> addNames = null;
            // 추가 재료 ID 이름 변환
            if (!addIds.isEmpty()) {
                log.info("추가 식재료 존재!");
                ResponseEntity<List<IngredientNameResDto>> addIngredientNames = cookFeignClient.getIngredientNames(addIds);
                if (addIngredientNames.getBody() == null || addIngredientNames.getBody().isEmpty()) {
                    throw new CustomException("추가 식재료 정보를 불러오지 못했습니다.", HttpStatus.BAD_REQUEST);
                }

                addNames = addIngredientNames.getBody().stream()
                        .map(IngredientNameResDto::getIngreName)
                        .toList();
            }

            // 요리 이름 조회
            String cookName = cookNameMap.get(cartCook.getCookId());

            result.add(CookDetailWithIngredientsResDto.builder()
                    .cookName(cookName)
                    .baseIngredients(baseNames)
                    .additionalIngredients(addNames)
                    .build());
        }

        return result;
    }

}
