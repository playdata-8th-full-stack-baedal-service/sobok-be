package com.sobok.paymentservice.payment.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@ToString
@Table(name = "cart_ingre", indexes = {
    @Index(name = "idx_cart_ingredient_cookId_defaultIngre", columnList = "cartCookId, defaultIngre")
})
public class CartIngredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long cartCookId;

    @Column(nullable = false)
    private Long ingreId;

    @Column(nullable = false)
    @Builder.Default
    private Integer unitQuantity = 1;

    @Column(nullable = false)
    @Builder.Default
    private String defaultIngre = "Y";
}
