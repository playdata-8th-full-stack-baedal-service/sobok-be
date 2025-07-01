package com.sobok.shopservice.shop.repository;

import com.sobok.shopservice.shop.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRepository extends JpaRepository<Shop, Long> {
}
