package com.sobok.cookservice.cook.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cook_monthly_hot")
public class CookOrderCountCache {
    @Id
    private Long cookId;

    @Column(nullable = false)
    private Integer orderCount;
}
