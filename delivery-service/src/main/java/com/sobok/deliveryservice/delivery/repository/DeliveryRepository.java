package com.sobok.deliveryservice.delivery.repository;

import com.sobok.deliveryservice.delivery.entity.Delivery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    Optional<Delivery> findByPaymentId(Long paymentId);

    List<Delivery> findByShopId(Long shopId);

    List<Delivery> findAllByShopIdIn(List<Long> shopIdList);

    Page<Delivery> findAllByRiderIdAndCompleteTimeIsNull(Long riderId, Pageable pageable);

    Page<Delivery> findAllByRiderId(Long riderId, Pageable pageable);


    @Query("select d from Delivery d where d.shopId in :shopIdList and d.riderId is null")
    List<Delivery> findAllByShopIdInAndRiderIdIsNull(@Param("shopIdList") List<Long> shopIdList);
}
