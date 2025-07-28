package com.sobok.paymentservice.payment.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cart_cook", indexes = {
        @Index(name = "idx_CartCook_paymentId", columnList = "paymentId"),
        @Index(name = "idx_CartCook_userId", columnList = "userId"),
        @Index(name = "idx_CartCook_cookId", columnList = "cookId")
})
@Builder
@ToString
public class CartCook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Setter
    private Long paymentId;

    @Column(nullable = false)
    private Long cookId;

    @Column(nullable = false)
    @ColumnDefault("1")
    private Integer count;


    public void changeCount(Integer count) {
        this.count = count;
    }

    public void detachFromPayment() {
        this.paymentId = null;
    }
}