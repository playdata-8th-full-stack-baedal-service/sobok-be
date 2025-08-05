package com.sobok.paymentservice.payment.dto.cart;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "삭제할 장바구니 요청 DTO")
public class DeleteCartReqDto {
    @Schema(description = "삭제할 장바구니 ID 리스트", example = "[12, 15, 18]")
    List<Long> cartCookIdList;
}
