package com.sobok.shopservice.shop.service;

import com.sobok.shopservice.shop.dto.stock.StockReqDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StockServiceTest {

    @Autowired
    private StockService stockService;

    @Test
    @DisplayName("stockDummy")
    void createStockDummy() {
        for (long i = 1L; i < 101L; i++) {
            stockService.registerStock(new StockReqDto(2L, i, 300));
        }
    }


}