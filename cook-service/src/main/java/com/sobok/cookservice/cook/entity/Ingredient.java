package com.sobok.cookservice.cook.entity;

import com.sobok.cookservice.cook.dto.request.IngreEditReqDto;
import com.sobok.cookservice.cook.dto.request.IngreReqDto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.StringUtils;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@ToString
@Table(name = "ingredient")
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ingre_name", nullable = false, unique = true)
    private String ingreName;

    @Column(nullable = false)
    private String price;

    @Column(nullable = false)
    private String origin;

    @Column(nullable = false)
    private String unit;

    public void update(IngreEditReqDto dto) {
        if (StringUtils.hasText(dto.getIngreName())) this.ingreName = dto.getIngreName();
        if (StringUtils.hasText(dto.getPrice())) this.price = dto.getPrice();
        if (StringUtils.hasText(dto.getOrigin())) this.origin = dto.getOrigin();
        if (StringUtils.hasText(dto.getUnit())) this.unit = dto.getUnit();
    }


}
