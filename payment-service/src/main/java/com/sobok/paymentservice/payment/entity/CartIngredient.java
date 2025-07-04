package com.sobok.paymentservice.payment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "cart_ingre")
public class CartIngredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long cartCookId;

    @Column(nullable = false)
    private Long ingreId;

    @Column(nullable = false)
    private Integer unitQuantity = 1;

    @Column(nullable = false)
    private String defaultIngre = "Y";
}
