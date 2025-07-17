package com.sobok.deliveryservice.delivery.service;

import com.sobok.deliveryservice.common.dto.TokenUserInfo;
import com.sobok.deliveryservice.common.exception.CustomException;
import com.sobok.deliveryservice.delivery.client.PaymentFeignClient;
import com.sobok.deliveryservice.delivery.client.ShopFeignClient;
import com.sobok.deliveryservice.delivery.client.UserFeignClient;
import com.sobok.deliveryservice.delivery.dto.info.UserAddressDto;
import com.sobok.deliveryservice.delivery.dto.payment.DeliveryRegisterDto;
import com.sobok.deliveryservice.delivery.dto.payment.RiderPaymentInfoResDto;
import com.sobok.deliveryservice.delivery.dto.payment.ShopPaymentResDto;
import com.sobok.deliveryservice.delivery.dto.request.AcceptOrderReqDto;
import com.sobok.deliveryservice.delivery.dto.response.DeliveryAvailOrderResDto;
import com.sobok.deliveryservice.delivery.dto.response.DeliveryAvailShopResDto;
import com.sobok.deliveryservice.delivery.dto.response.DeliveryOrderResDto;
import com.sobok.deliveryservice.delivery.dto.response.DeliveryResDto;
import com.sobok.deliveryservice.delivery.entity.Delivery;
import com.sobok.deliveryservice.delivery.entity.Rider;
import com.sobok.deliveryservice.delivery.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final ShopFeignClient shopFeignClient;
    private final PaymentFeignClient paymentFeignClient;
    private final UserFeignClient userFeignClient;
    private final RiderService riderService;

    public void registerDelivery(DeliveryRegisterDto reqDto) {
        // 배달 객체 생성
        Delivery delivery = Delivery.builder()
                .shopId(reqDto.getShopId())
                .paymentId(reqDto.getPaymentId())
                .build();

        deliveryRepository.save(delivery);
    }

    /**
     * 라이더 정보 조회 (전체 주문 조회용)
     */
    public RiderPaymentInfoResDto getRiderInfoByPaymentId(Long paymentId) {
        Delivery delivery = deliveryRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new CustomException("배달 정보 없음", HttpStatus.NOT_FOUND));

        Rider rider = riderService.findRiderByIdOrThrow(delivery.getRiderId());

        return RiderPaymentInfoResDto.builder()
                .riderId(rider.getId())
                .riderName(rider.getName())
                .riderPhone(rider.getPhone())
                .build();
    }

    public DeliveryResDto getDelivery(Long paymentId) {
        Delivery delivery = deliveryRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new CustomException("해당 배달 목록이 존재하지 않습니다.", HttpStatus.NOT_FOUND));
        return DeliveryResDto.builder()
                .shopId(delivery.getShopId())
                .completeTime(delivery.getCompleteTime())
                .riderId(delivery.getRiderId())
                .build();
    }

    public List<Long> getPaymentId(Long shopId) {
        return deliveryRepository.findByShopId(shopId).stream()
                .map(Delivery::getPaymentId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

    }

    /**
     * 배달 가능 주문 목록 조회
     */
    public List<DeliveryAvailOrderResDto> getAvailableOrders(
            TokenUserInfo userInfo, Double latitude, Double longitude,
            Long pageNo, Long numOfRows
    ) {
        //라이더 검증
        riderService.existsByIdOrThrow(userInfo.getRiderId());

        //근처 가게 정보 목록 조회
        List<DeliveryAvailShopResDto> nearShop = shopFeignClient.getNearShop(latitude, longitude);
        log.info("nearShop: {}", nearShop);

        if (nearShop.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, DeliveryAvailShopResDto> shopMap = nearShop.stream()
                .collect(Collectors.toMap(DeliveryAvailShopResDto::getShopId, Function.identity()));
        List<Long> shopIdList = new ArrayList<>(shopMap.keySet());

        Pageable pageable = PageRequest.of(pageNo.intValue() - 1, numOfRows.intValue());

        Page<Delivery> deliveryPage = deliveryRepository.findAllByShopIdIn(shopIdList, pageable);
        log.info("deliveryPage: {}", deliveryPage);

        Map<Long, Delivery> paymentIdToDeliveryMap = deliveryPage.getContent().stream()
                .collect(Collectors.toMap(Delivery::getPaymentId, Function.identity()));
        if (paymentIdToDeliveryMap.isEmpty()) {
            return Collections.emptyList();
        }

        // 배달 전인 주문 조회 (Payment 정보)
        List<Long> paymentIdList = new ArrayList<>(paymentIdToDeliveryMap.keySet());
        List<ShopPaymentResDto> riderAvailPayment = paymentFeignClient.getRiderAvailPayment(paymentIdList);
        log.info("배달 가능한 주문들 Payment: {}", riderAvailPayment);

        if (riderAvailPayment.isEmpty()) {
            return Collections.emptyList();
        }

        // 주소 정보 조회
        Set<Long> addressIdSet = riderAvailPayment.stream()
                .map(ShopPaymentResDto::getUserAddressId)
                .collect(Collectors.toSet());

        Map<Long, UserAddressDto> addressMap = userFeignClient.getUserAddressInfo(new ArrayList<>(addressIdSet)).stream()
                .collect(Collectors.toMap(UserAddressDto::getId, Function.identity()));

        return riderAvailPayment.stream()
                .map(payment -> {
                    Delivery delivery = paymentIdToDeliveryMap.get(payment.getPaymentId());
                    DeliveryAvailShopResDto shop = shopMap.get(delivery.getShopId());
                    UserAddressDto address = addressMap.get(payment.getUserAddressId());

                    return DeliveryAvailOrderResDto.builder()
                            .deliveryId(delivery.getId())
                            .shopId(shop.getShopId())
                            .shopName(shop.getShopName())
                            .shopRoadFull(shop.getRoadFull())
                            .paymentId(payment.getPaymentId())
                            .orderId(payment.getOrderId())
                            .orderState(payment.getOrderState())
                            .createdAt(payment.getCreatedAt())
                            .updatedAt(payment.getUpdatedAt())
                            .roadFull(address != null ? address.getRoadFull() : null)
                            .addrDetail(address != null ? address.getAddrDetail() : null)
                            .build();
                })
                .filter(dto -> dto.getShopName() != null && dto.getRoadFull() != null)
                .toList();
    }

    /**
     * 배달 중인 목록 조회
     */
    public List<DeliveryOrderResDto> getDeliveringOrders(TokenUserInfo userInfo, Long pageNo, Long numOfRows) {
        return getRiderDeliveries(userInfo, pageNo, numOfRows, true);
    }

    /**
     * 배달 전체 목록 조회
     */
    public List<DeliveryOrderResDto> getDeliveryOrders(TokenUserInfo userInfo, Long pageNo, Long numOfRows) {
        return getRiderDeliveries(userInfo, pageNo, numOfRows, false);
    }

    // 응답: 주문 번호, 가게 이름, 가게 주소, 배달지, 완료 시간
    // payment: paymentId, orderId, userAddressId
    // user-service: userAddressId로 주소 가져오기
    // shop-service: deliveryList.getShopId로 가게 이름, 가게 주소 가져오기
    private List<DeliveryOrderResDto> getRiderDeliveries(TokenUserInfo userInfo, Long pageNo, Long numOfRows, boolean delivering) {
        // 1. 라이더 검증
        riderService.existsByIdOrThrow(userInfo.getRiderId());

        // 2. 배달 목록 조회 (페이징 포함)
        Pageable pageable = PageRequest.of(pageNo.intValue() - 1, numOfRows.intValue(), Sort.by(Sort.Direction.DESC, "id"));

        Page<Delivery> deliveryPage = delivering
                ? deliveryRepository.findAllByRiderIdAndCompleteTimeIsNull(userInfo.getRiderId(), pageable)
                : deliveryRepository.findAllByRiderId(userInfo.getRiderId(), pageable);

        List<Delivery> deliveryList = deliveryPage.getContent();
        if (deliveryList.isEmpty()) return Collections.emptyList();

        // 3. paymentId 리스트로 Payment 정보 조회
        List<Long> paymentIds = deliveryList.stream().map(Delivery::getPaymentId).toList();
        List<ShopPaymentResDto> paymentList = paymentFeignClient.getRiderPayment(paymentIds);
        if (paymentList.isEmpty()) return Collections.emptyList();

        Map<Long, ShopPaymentResDto> paymentMap = paymentList.stream()
                .collect(Collectors.toMap(ShopPaymentResDto::getPaymentId, Function.identity()));

        // 4. userAddressId → 주소 정보 조회
        Set<Long> addressIds = paymentList.stream()
                .map(ShopPaymentResDto::getUserAddressId)
                .collect(Collectors.toSet());
        Map<Long, UserAddressDto> addressMap = userFeignClient.getUserAddressInfo(new ArrayList<>(addressIds)).stream()
                .collect(Collectors.toMap(UserAddressDto::getId, Function.identity()));

        // 5. shopId → 가게 정보 조회
        Set<Long> shopIds = deliveryList.stream().map(Delivery::getShopId).collect(Collectors.toSet());
        List<DeliveryAvailShopResDto> shopList = shopFeignClient.getShopInfoByIds(new ArrayList<>(shopIds));
        Map<Long, DeliveryAvailShopResDto> shopMap = shopList.stream()
                .collect(Collectors.toMap(DeliveryAvailShopResDto::getShopId, Function.identity()));

        return deliveryList.stream()
                .map(delivery -> {
                    ShopPaymentResDto payment = paymentMap.get(delivery.getPaymentId());
                    UserAddressDto address = addressMap.get(payment.getUserAddressId());
                    DeliveryAvailShopResDto shop = shopMap.get(delivery.getShopId());

                    return DeliveryOrderResDto.builder()
                            .orderId(payment.getOrderId())
                            .paymentId(payment.getPaymentId())
                            .orderState(payment.getOrderState())
                            .shopName(shop.getShopName())
                            .shopRoadFull(shop.getRoadFull())
                            .roadFull(address != null ? address.getRoadFull() : null)
                            .addrDetail(address != null ? address.getAddrDetail() : null)
                            .completeTime(delivery.getCompleteTime())
                            .build();
                })
                .filter(dto -> dto.getShopName() != null && dto.getRoadFull() != null)
                .toList();
    }

    /**
     * 라이더 주문 선택
     */
    @Transactional
    public void acceptDelivery(AcceptOrderReqDto acceptOrderReqDto) {
        //라이더 검증
        riderService.existsByIdOrThrow(acceptOrderReqDto.getRiderId());

        //delivery 테이블에 riderId, completeTime 널인지 확인
        Delivery delivery = deliveryRepository.findByPaymentId(acceptOrderReqDto.getPaymentId())
                .orElseThrow(() -> new CustomException("해당 배달 목록이 존재하지 않습니다.", HttpStatus.NOT_FOUND));

        if (delivery.getRiderId() != null || delivery.getCompleteTime() != null) {
            throw new CustomException("이미 지정된 주문입니다.", HttpStatus.BAD_REQUEST);
        }

        delivery.updateRiderId(acceptOrderReqDto.getRiderId());
        deliveryRepository.save(delivery);
        log.info("riderId 업데이트 완료");
    }

    /**
     * 라이더 주문 선택
     */
    @Transactional
    public void deliveryComplete(AcceptOrderReqDto acceptOrderReqDto) {
        //라이더 검증
        riderService.existsByIdOrThrow(acceptOrderReqDto.getRiderId());

        Delivery delivery = deliveryRepository.findByPaymentId(acceptOrderReqDto.getPaymentId())
                .orElseThrow(() -> new CustomException("해당 배달 목록이 존재하지 않습니다.", HttpStatus.NOT_FOUND));

        if (delivery.getCompleteTime() != null) {
            throw new CustomException("이미 배달 완료된 주문입니다.", HttpStatus.BAD_REQUEST);
        }

        if (!Objects.equals(acceptOrderReqDto.getRiderId(), delivery.getRiderId())) {
            throw new CustomException("본인 담당 주문이 아닙니다.", HttpStatus.BAD_REQUEST);
        }

        delivery.setCompleteTime(LocalDateTime.now());
        deliveryRepository.save(delivery);
        log.info("completeTime 업데이트 완료");
    }

    /**
     * paymentId를 기준으로 delivery 테이블에서 shopId를 찾아서 반환
     */
    public Long getShopIdByPaymentId(Long paymentId) {
        Delivery delivery = deliveryRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new CustomException("배달 정보가 없습니다.", HttpStatus.NOT_FOUND));
        return delivery.getShopId();
    }
}