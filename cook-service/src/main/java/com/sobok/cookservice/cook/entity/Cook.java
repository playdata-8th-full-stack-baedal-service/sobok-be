package com.sobok.cookservice.cook.entity;

import com.sobok.cookservice.common.entity.BaseTimeEntity;
import com.sobok.cookservice.common.enums.CookCategory;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "cook", indexes = {
        @Index(name = "idx_cook_category_active", columnList = "active, category"),
        @Index(name = "idx_cook_updatedAt", columnList = "updatedAt")
})
public class Cook extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String allergy;

    @Column(nullable = false)
    private String recipe;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CookCategory category;

    @Column(nullable = false, unique = true)
    private String thumbnail;

    @Builder.Default
    @Column(nullable = false)
    private String active = "Y";

}
