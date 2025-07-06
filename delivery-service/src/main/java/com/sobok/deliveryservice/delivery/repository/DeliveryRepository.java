package com.sobok.deliveryservice.delivery.repository;

import com.sobok.deliveryservice.delivery.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
}
