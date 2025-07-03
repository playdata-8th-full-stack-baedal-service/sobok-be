package com.sobok.cookservice.cook.entity;

import com.sobok.cookservice.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "cook")
public class Cook extends BaseTimeEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String allergy;

    @Column(nullable = false)
    private String recipe;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false, unique = true)
    private String thumbnail;

}
