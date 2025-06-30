package com.sobok.deliveryservice.delivery.repository;

import com.sobok.deliveryservice.delivery.entity.Rider;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiderRepository extends JpaRepository<Rider, Integer> {
    boolean existsByPhone(String phone);
    boolean existsByPermissionNumber(String permissionNumber);
}
