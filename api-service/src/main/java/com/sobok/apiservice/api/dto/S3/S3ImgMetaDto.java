package com.sobok.apiservice.api.dto.S3;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class S3ImgMetaDto {
    String fileName;
    String category;
}
