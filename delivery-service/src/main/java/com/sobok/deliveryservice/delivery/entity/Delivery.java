package com.sobok.deliveryservice.delivery.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "delivery", indexes = {
        @Index(name = "idx_delivery_paymentId", columnList = "paymentId")
})
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    private Long shopId;

    private Long riderId;

    @Column(nullable = false)
    private Long paymentId;

    @Setter
    private LocalDateTime completeTime;

    public void updateRiderId(Long riderId) {
        this.riderId = riderId;
    }
}
