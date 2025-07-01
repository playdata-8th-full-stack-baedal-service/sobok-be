package com.sobok.cookservice.cook.repository;

import com.sobok.cookservice.cook.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    Optional<Ingredient> findByIngreName(String ingreName);

}
