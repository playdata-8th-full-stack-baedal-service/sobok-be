package com.sobok.shopservice.shop.service;

import com.sobok.shopservice.common.exception.CustomException;
import com.sobok.shopservice.shop.dto.stock.StockReqDto;
import com.sobok.shopservice.shop.dto.stock.StockResDto;
import com.sobok.shopservice.shop.entity.Stock;
import com.sobok.shopservice.shop.repository.StockQueryRepository;
import com.sobok.shopservice.shop.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StockService {
    private final StockQueryRepository queryRepository;
    private final StockRepository stockRepository;

    /**
     * 재고 등록
     */
    public StockResDto registerStock(StockReqDto reqDto) {
        // 해당 가게에 이미 식재료가 존재하는지 확인
        if (queryRepository.checkStock(reqDto) != null) {
            throw new CustomException("이미 존재하는 식재료가 있습니다.", HttpStatus.BAD_REQUEST);
        }

        // 재고량 검증
        if (reqDto.getQuantity() <= 0) {
            throw new CustomException("잘못된 재고량 입력입니다.", HttpStatus.BAD_REQUEST);
        }

        // 재고 등록
        Stock result = stockRepository.save(reqDto.toEntity());

        // 응답 생성
        return StockResDto.toDto(result);
    }

    /**
     * 재고 변경
     */
    public StockResDto deductStock(StockReqDto reqDto) {
        // 식재료 검증
        Stock stock = queryRepository.checkStock(reqDto);
        if (stock == null) {
            throw new CustomException("존재하는 식재료가 없습니다.", HttpStatus.BAD_REQUEST);
        }

        // 변경 사항 없다면 재고량 검사 진행하지 않음
        if (reqDto.getQuantity() == 0) {
            return StockResDto.toDto(stock);
        }

        // 재고량 검증
        stock.updateQuantity(reqDto.getQuantity());
        if (stock.getQuantity() < 0) {
            throw new CustomException("잘못된 재고량 입력 입니다.", HttpStatus.BAD_REQUEST);
        }

        // 응답 생성
        return StockResDto.toDto(stockRepository.save(stock));
    }


    /**
     * 재고 조회
     */
    public List<StockResDto> getStock(Long shopId) {
        return queryRepository.getStockByShopId(shopId);
    }
}
