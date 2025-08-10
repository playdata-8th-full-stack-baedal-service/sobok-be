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
        for (long i = 101L; i < 158L; i++) {

            stockService.registerStock(new StockReqDto(1L, i,  (int) (1 + 10 * Math.random())));
            stockService.registerStock(new StockReqDto(2L, i,  (int) (1 + 10 * Math.random())));
            stockService.registerStock(new StockReqDto(3L, i,  (int) (1 + 10 * Math.random())));

            double random = Math.random();
            if (random < 0.3) {
                stockService.deductStock(new StockReqDto(1L, i, (int) (1000 + 500 * Math.random())));
            }

            random = Math.random();
            if (random < 0.5) {
                stockService.deductStock(new StockReqDto(2L, i, (int) (1000 + 500 * Math.random())));
            }

            random = Math.random();
            if (random < 0.9) {
                stockService.deductStock(new StockReqDto(3L, i, (int) (1000 + 500 * Math.random())));
            }
        }
    }


}