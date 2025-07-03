package com.sobok.cookservice.cook.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@ToString
@Table(name = "ingredient")
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ingre_name", nullable = false, unique = true)
    private String ingreName;

    @Column(nullable = false)
    private String price;

    @Column(nullable = false)
    private String origin;

    @Column(nullable = false)
    private String unit;

}
