package com.sobok.cookservice.cook.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.sobok.cookservice.common.enums.CookCategory;
import com.sobok.cookservice.cook.dto.display.BasicCookDisplay;
import com.sobok.cookservice.cook.dto.display.DisplayParamDto;
import com.sobok.cookservice.cook.entity.QCook;
import com.sobok.cookservice.cook.entity.QCookOrderCountCache;
import com.sobok.cookservice.cook.repository.CookQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.sobok.cookservice.cook.entity.QCook.cook;
import static com.sobok.cookservice.cook.entity.QCookOrderCountCache.cookOrderCountCache;

@Service
@Slf4j
@RequiredArgsConstructor
public class CookDisplayService {
    private final CookQueryRepository cookQueryRepository;

    public List<BasicCookDisplay> getCooks(DisplayParamDto params) {
        BooleanBuilder builder = new BooleanBuilder();
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        // update 순 기본 정렬
        orderSpecifiers.add(cook.updatedAt.desc());

        if(params.getSort() != null && params.getSort().equals("order")) {
            orderSpecifiers.add(0, cookOrderCountCache.orderCount.desc());
        }

        if (params.getCategory() != null) {
            CookCategory cookCategory = CookCategory.valueOf(params.getCategory().toUpperCase());
            builder.and(cook.category.eq(cookCategory));
        }

        if (params.getKeyword() != null) {
            builder.and(cook.name.like("%" + params.getKeyword() + "%"));
        }

        builder.and(cook.active.eq("Y"));

        return cookQueryRepository.getCookDisplaysByCondition(params, builder, orderSpecifiers);
    }
}
