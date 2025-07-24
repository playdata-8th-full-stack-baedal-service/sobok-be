package com.sobok.cookservice.cook.service;

import com.querydsl.core.BooleanBuilder;
import com.sobok.cookservice.common.enums.CookCategory;
import com.sobok.cookservice.cook.dto.display.BasicCookDisplay;
import com.sobok.cookservice.cook.dto.display.DisplayParamDto;
import com.sobok.cookservice.cook.entity.QCook;
import com.sobok.cookservice.cook.repository.CookQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.sobok.cookservice.cook.entity.QCook.cook;

@Service
@Slf4j
@RequiredArgsConstructor
public class CookDisplayService {
    private final CookQueryRepository cookQueryRepository;

    public List<BasicCookDisplay> getCooks(DisplayParamDto params) {
        if (params.getSort() != null && params.getSort().equals("order")) {
            // TODO : 주문량 조회
        }

        BooleanBuilder builder = new BooleanBuilder();

        if (params.getCategory() != null) {
            CookCategory cookCategory = CookCategory.valueOf(params.getCategory().toUpperCase());
            builder.and(cook.category.eq(cookCategory));
        }

        if (params.getKeyword() != null) {
            builder.and(cook.name.like("%" + params.getKeyword() + "%"));
        }

        builder.and(cook.active.eq("Y"));

        return cookQueryRepository.getCookDisplaysByCondition(params, builder);
    }
}
