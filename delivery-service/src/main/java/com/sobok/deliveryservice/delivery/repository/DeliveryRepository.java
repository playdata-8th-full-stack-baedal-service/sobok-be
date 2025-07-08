package com.sobok.deliveryservice.delivery.repository;

import com.sobok.deliveryservice.delivery.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    Optional<Delivery> findByPaymentId(Long paymentId);
}
