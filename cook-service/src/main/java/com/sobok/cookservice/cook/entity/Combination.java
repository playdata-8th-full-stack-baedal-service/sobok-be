package com.sobok.cookservice.cook.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "combination")
public class Combination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cook_id")
    private Long cookId;

    @Column(name = "ingre_id")
    private Long ingreId;

    @Column(name = "unit_quantity")
    private Integer unitQuantity;

    @ManyToOne(fetch = FetchType.LAZY) // 식재료 테이블과 연관관계 설정
    @JoinColumn(name = "ingre_id", insertable = false, updatable = false) // db에 저장 x
    private Ingredient ingredient;
}
