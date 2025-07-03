package com.sobok.cookservice.cook.repository;

import com.sobok.cookservice.cook.dto.response.IngreResDto;
import com.sobok.cookservice.cook.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    Boolean existsByIngreName(String ingreName);

//    List<IngreResDto> keywordSearch(String keyword);

    @Query("SELECT i FROM Ingredient i WHERE i.ingreName LIKE %:keyword% ORDER BY i.ingreName ASC")
    List<Ingredient> keywordSearch(@Param("keyword") String keyword);
}
