package com.sobok.deliveryservice.delivery.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "rider")
public class Rider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "auth_id", nullable = false)
    private Long authId;

    @Column(nullable = false)
    public String name;

    @Column(unique = true, nullable = false)
    public String phone;

    @Column(name = "permission_number", nullable = false, unique = true)
    public String permissionNumber;

}
