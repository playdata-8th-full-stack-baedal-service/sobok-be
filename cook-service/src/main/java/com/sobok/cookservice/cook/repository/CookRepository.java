package com.sobok.cookservice.cook.repository;

import com.sobok.cookservice.cook.entity.Cook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CookRepository extends JpaRepository<Cook, Long> {
}
