package com.sobok.shopservice.shop.repository;

import com.sobok.shopservice.shop.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long> {
    Optional<Shop> findByShopName(String shopName);
    Optional<Shop> findByRoadFull(String roadFull);
    Optional<Shop> findByPhone(String phoneNumber);


    boolean existsByShopName(String shopName);
    boolean existsByRoadFull(String roadFull);

    Optional<Shop> findByAuthId(Long authId);
}
