package com.sobok.cookservice.cook.service;

import com.sobok.cookservice.cook.entity.Combination;
import com.sobok.cookservice.cook.repository.CombinationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CombinationService {
    private final CombinationRepository combinationRepository;


    /**
     * 식재료 id와 필요 단위 수량을 맵핑
     */
    public HashMap<Long, Integer> getDefaultIngreInfoList(Long cookId) {
        return combinationRepository.findByCookId(cookId)
                .stream()
                .collect(
                        Collectors.toMap(
                                Combination::getIngreId, // Key
                                Combination::getUnitQuantity, // Value
                                (a, b) -> b, // 같은 키에 대한 충돌 처리
                                HashMap::new
                        )
                );

    }
}
