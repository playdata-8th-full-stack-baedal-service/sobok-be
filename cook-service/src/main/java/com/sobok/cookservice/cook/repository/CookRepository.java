package com.sobok.cookservice.cook.repository;

import com.sobok.cookservice.cook.entity.Cook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CookRepository extends JpaRepository<Cook, Long> {
    Optional<Cook> findByName(String name);
    Optional<Cook> findByThumbnail(String thumbnailUrl);
    boolean existsById(Long id);
    List<Cook> findByIdIn(List<Long> ids);

    List<Cook> findByIdNotIn(Collection<Long> ids);

    boolean existsByIdAndActive(Long id, String active);
    List<Cook> findAllByIdInAndActive(List<Long> ids, String active);

    List<Cook> getCooksById(Long id);
}
