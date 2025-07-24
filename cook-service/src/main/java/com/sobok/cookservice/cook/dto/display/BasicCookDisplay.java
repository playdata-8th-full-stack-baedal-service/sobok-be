package com.sobok.cookservice.cook.dto.display;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BasicCookDisplay {
    private Long cookId;
    private String cookName;
    private String thumbnail;

}
