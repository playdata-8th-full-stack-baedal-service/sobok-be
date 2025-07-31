package com.sobok.cookservice.cook.repository;

import com.sobok.cookservice.cook.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;


public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    Boolean existsByIngreName(String ingreName);
}
