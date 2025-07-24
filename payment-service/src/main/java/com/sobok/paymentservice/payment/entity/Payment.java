package com.sobok.paymentservice.payment.entity;

import com.sobok.paymentservice.common.entity.BaseTimeEntity;
import com.sobok.paymentservice.common.enums.OrderState;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "payment")
@Builder
@AllArgsConstructor
@ToString
public class Payment extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long totalPrice;

    private String payMethod;

    @Column(nullable = false)
    private Long userAddressId;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private OrderState orderState;

    @Column(nullable = false)
    private String riderRequest;

    private String paymentKey;

    private String orderId;

    public void completePayment(String paymentKey, String method, OrderState orderState) {
        this.paymentKey = paymentKey;
        this.payMethod = method;
        this.orderState = orderState;
    }

    public void nextState() {
        this.orderState = this.orderState.next();
    }
}
