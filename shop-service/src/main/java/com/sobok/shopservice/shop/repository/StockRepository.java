package com.sobok.shopservice.shop.repository;

import com.sobok.shopservice.shop.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, Long> {

}
