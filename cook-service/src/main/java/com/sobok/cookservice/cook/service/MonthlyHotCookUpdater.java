package com.sobok.cookservice.cook.service;

import com.sobok.cookservice.cook.dto.display.MonthlyHot;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MonthlyHotCookUpdater {
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void updateMonthlyHotCooks(List<MonthlyHot> monthlyHotList) {
        // 원래 테이블 비우기
        jdbcTemplate.execute("TRUNCATE TABLE cook_monthly_hot");

        // batch insert
        jdbcTemplate.batchUpdate(
                "INSERT INTO cook_monthly_hot (cook_id, order_count) VALUES (?,?)",
                monthlyHotList,
                100,
                (ps, order) -> {
                    ps.setLong(1, order.getCookId());
                    ps.setLong(2, order.getOrderCount());
                }
        );
    }
}
