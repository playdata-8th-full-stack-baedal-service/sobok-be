package com.sobok.cookservice.cook.repository;

import com.sobok.cookservice.cook.entity.Combination;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CombinationRepository extends JpaRepository<Combination, Long> {
    List<Combination> findByCookId(Long cookId);

    List<Combination> findByCookIdIn(List<Long> cookIds);
}
