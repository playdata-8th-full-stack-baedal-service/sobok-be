package com.sobok.cookservice.cook.dto.response;

import com.sobok.cookservice.common.enums.CookCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CookResDto {
    private Long id;
    private String name;
    private String allergy;
    private String recipe;
    private CookCategory category;
    private String thumbnail;
}
