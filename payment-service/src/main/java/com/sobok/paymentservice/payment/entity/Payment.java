package com.sobok.paymentservice.payment.entity;

import com.sobok.paymentservice.common.entity.BaseTimeEntity;
import com.sobok.paymentservice.common.enums.OrderState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "payment")
public class Payment extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long totalPrice;

    @Column(nullable = false)
    private String payMethod;

    @Column(nullable = false)
    private Long userAddressId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderState orderState;

    @Column(nullable = false)
    private String riderRequest;
}
