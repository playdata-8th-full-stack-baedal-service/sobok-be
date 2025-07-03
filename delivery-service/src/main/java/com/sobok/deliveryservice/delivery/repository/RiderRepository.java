package com.sobok.deliveryservice.delivery.repository;

import com.sobok.deliveryservice.delivery.entity.Rider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RiderRepository extends JpaRepository<Rider, Integer> {
    boolean existsByPhone(String phone); // 존재 여부만 확인하기 위해 boolean 타입 사용
    boolean existsByPermissionNumber(String permissionNumber);

    Optional<Rider> findByPhone(String phoneNumber);

    Optional<Rider> getRiderByAuthId(Long authId);
}
