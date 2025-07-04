package com.sobok.paymentservice.payment.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cart_cook")
@Builder
public class CartCook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = true)
    private Long paymentId;

    @Column(nullable = false)
    private Long cookId;

    @Column(nullable = false)
    @ColumnDefault("1")
    private Integer count;


    public void changeCount(Long count) {
        this.count = count;
    }
}