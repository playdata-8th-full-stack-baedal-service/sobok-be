package com.sobok.cookservice.cook.repository;

import com.sobok.cookservice.cook.entity.Cook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CookRepository extends JpaRepository<Cook, Long> {
    Optional<Cook> findByName(String name);
    Optional<Cook> findByThumbnail(String thumbnailUrl);
    boolean existsById(Long id);
}
