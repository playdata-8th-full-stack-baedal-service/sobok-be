package com.sobok.shopservice.shop.entity;

import com.sobok.shopservice.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(
        name = "stock",
        indexes = {
                // 조회 순서는 반드시 이 순서대로 해주세요.
                @Index(name = "idx_stock", columnList = "shopId, ingredientId, quantity")
        }
)
public class Stock extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long ingredientId;

    @Column(nullable = false)
    private Long shopId;

    @Column(nullable = false)
    private Integer quantity;

    public void updateQuantity(Integer quantity) {
        this.quantity = this.quantity + quantity;
    }
}
