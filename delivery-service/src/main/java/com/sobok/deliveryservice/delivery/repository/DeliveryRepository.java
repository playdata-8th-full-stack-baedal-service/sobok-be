package com.sobok.deliveryservice.delivery.repository;

import com.sobok.deliveryservice.delivery.entity.Delivery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    Optional<Delivery> findByPaymentId(Long paymentId);

    List<Delivery> findByShopId(Long shopId);

    Page<Delivery> findAllByShopIdIn(List<Long> shopIdList, Pageable pageable);

    Page<Delivery> findAllByRiderIdAndCompleteTimeIsNull(Long riderId, Pageable pageable);

    Page<Delivery> findAllByRiderId(Long riderId, Pageable pageable);


}
