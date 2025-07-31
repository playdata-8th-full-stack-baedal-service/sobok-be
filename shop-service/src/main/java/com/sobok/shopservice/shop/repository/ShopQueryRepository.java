package com.sobok.shopservice.shop.repository;

import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sobok.shopservice.shop.entity.QShop;
import com.sobok.shopservice.shop.entity.Shop;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
@Slf4j
public class ShopQueryRepository {
    private final JPAQueryFactory factory;

    public Shop getNearestShop(QShop shop, double minLat, double maxLat, double minLng, double maxLng, NumberTemplate<Double> distance) {
        return factory
                .select(shop)
                .from(shop)
                .where(
                        shop.latitude.between(minLat, maxLat),
                        shop.longitude.between(minLng, maxLng)
                )
                .orderBy(distance.asc())
                .limit(1)
                .fetchFirst();
    }


    public List<Shop> getShopsByDistance(QShop shop, double minLat, double maxLat, double minLng, double maxLng, NumberTemplate<Double> distance) {
        return factory
                .select(shop)
                .from(shop)
                .where(
                        shop.latitude.between(minLat, maxLat),
                        shop.longitude.between(minLng, maxLng)
                )
                .orderBy(distance.asc())
                .fetch();
    }
}
