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
@Table(name = "delivery")
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    private Long shopId;

    @Column(nullable = true)
    private Long riderId;

    @Column(nullable = false)
    private Long paymentId;

    @Column(nullable = true)
    private LocalDateTime completeTime;
}
