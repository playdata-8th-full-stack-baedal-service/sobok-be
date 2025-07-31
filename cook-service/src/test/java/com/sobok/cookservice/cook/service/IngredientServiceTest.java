package com.sobok.cookservice.cook.service;

import com.sobok.cookservice.cook.controller.CookController;
import com.sobok.cookservice.cook.dto.request.IngreReqDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class IngredientServiceTest {

    @Autowired
    private IngredientService ingredientService;

    List<String> names = List.of(
            "양파", "마늘", "대파", "고추", "당근", "감자", "배추", "무", "상추", "오이",
            "호박", "가지", "토마토", "브로콜리", "버섯", "시금치", "부추", "콩나물", "숙주", "미나리",
            "청경채", "피망", "파프리카", "샐러리", "아스파라거스", "콜리플라워", "연근", "우엉", "쑥갓", "깻잎",
            "레몬", "라임", "사과", "배", "바나나", "포도", "딸기", "블루베리", "키위", "오렌지",
            "파인애플", "망고", "자두", "복숭아", "체리", "멜론", "수박", "감", "귤", "석류",
            "돼지고기", "소고기", "닭고기", "양고기", "오리고기", "베이컨", "햄", "소시지", "참치", "연어",
            "고등어", "갈치", "꽁치", "명태", "오징어", "낙지", "문어", "새우", "게", "조개",
            "홍합", "바지락", "멸치", "다시마", "미역", "김", "굴", "가리비", "전복", "해삼",
            "두부", "계란", "우유", "치즈", "버터", "요거트", "식빵", "밥", "라면", "국수",
            "밀가루", "설탕", "소금", "간장", "고추장", "된장", "식초", "참기름", "들기름", "올리브유"
    );

    List<String> origins = List.of(
            "대한민국", "중국", "일본", "미국", "호주", "베트남", "태국", "인도", "멕시코", "프랑스"
    );

    List<Integer> units = List.of(
            1, 10, 30, 50, 100
    );


    @Test
    @DisplayName("ingredientRegister")
    void ingredientRegisterTest() {
        // given
        for (int i = 0; i < names.size(); i++) {
            int originIndex = Math.toIntExact(Math.round(Math.random() * (origins.size() - 1)));
            int unitIndex = Math.toIntExact(Math.round(Math.random() * (units.size() - 1)));
            int price = Math.toIntExact(Math.round(Math.random() * 30 + 1));


            IngreReqDto reqDto = IngreReqDto.builder()
                    .id(0L)
                    .ingreName(names.get(i))
                    .price(price)
                    .unit(units.get(unitIndex))
                    .origin(origins.get(originIndex))
                    .build();

            ingredientService.ingreCreate(reqDto);
        }


        // when

        // then

    }

}