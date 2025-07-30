package com.sobok.cookservice;

import com.sobok.cookservice.common.enums.CookCategory;
import com.sobok.cookservice.cook.dto.request.CookCreateReqDto;
import com.sobok.cookservice.cook.service.CookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class CookServiceApplicationTests {

    @Autowired
    private CookService cookService;

    List<String> names = List.of(
             "비빔밥", "불고기", "잡채", "제육볶음", "순두부찌개", "갈비탕", "삼계탕", "오징어볶음",
            "떡볶이", "순대국", "김밥", "라면", "우동", "칼국수", "냉면", "쫄면", "만두", "부침개",
            "파전", "해물파전", "닭갈비", "찜닭", "감자탕", "콩나물국밥", "해장국", "설렁탕", "곰탕", "육개장",
            "볶음밥", "김치볶음밥", "계란찜", "계란말이", "두부조림", "멸치볶음", "장조림", "소세지볶음", "오므라이스", "돈까스",
            "함박스테이크", "스파게티", "리조또", "피자", "햄버거", "샌드위치", "토스트", "시리얼", "콘스프", "수프",
            "스테이크", "타코", "케밥", "샤오롱바오", "마라탕", "훠궈", "짜장면", "짬뽕", "탕수육", "깐풍기",
            "양장피", "마파두부", "베트남쌀국수", "반미", "카오팟", "팟타이", "볶음쌀국수", "나시고렝", "미고랭", "사테",
            "까르보나라", "봉골레파스타", "알리오올리오", "크림스튜", "로스트치킨", "그릴연어", "훈제오리", "잡곡밥", "현미밥", "곤약밥",
            "샐러드", "닭가슴살스테이크", "연어샐러드", "토마토파스타", "치킨텐더", "감자튀김", "치즈볼", "치킨너겟", "양념치킨", "후라이드치킨",
            "마늘치킨", "간장치킨", "핫도그", "브리또", "에그스크램블", "팬케이크", "와플", "호떡", "붕어빵", "계란빵"
    );

    List<String> allergies = List.of(
            "알레르기 유발 식재료 없음", "계란, 우유", "밀, 대두", "새우", "게, 조개류", "땅콩, 견과류", "생선", "메밀", "돼지고기", "닭고기"
    );

    List<String> recipes = List.of(
            "재료를 손질한 뒤 팬에 볶는다. 간을 맞추고 익을 때까지 조리한다. 접시에 담아낸다.",
            "냄비에 육수를 붓고 재료를 넣는다. 끓으면 양념을 추가한다. 약불로 졸인 후 완성한다.",
            "야채를 썰어 밥 위에 올린다. 고추장과 참기름을 뿌리고 비빈다. 계란 후라이를 올린다.",
            "고기를 양념에 재운다. 팬에 구워 익히고 접시에 담는다. 채소와 함께 낸다.",
            "국물을 내고 면을 삶는다. 고명과 함께 그릇에 담아 낸다. 김가루를 뿌린다.",
            "재료를 썰어 볶는다. 양념을 더하고 졸인다. 통깨를 뿌려 마무리한다."
    );

    List<CookCategory> categories = Arrays.stream(CookCategory.values()).toList();

    List<String> thumbnails = List.of(
        "https://d3c5012dwkvoyc.cloudfront.net/food/d6f11d9d-47d8-4670-89a9-91f53bc35b80kimchijeon.webp",
            "https://d3c5012dwkvoyc.cloudfront.net/food/0d61d7ba-b383-401f-9ad8-2c80169479cdargo.jpg",
            "https://d3c5012dwkvoyc.cloudfront.net/food/113ef525-889c-435f-8b71-cc0a9bf3e3bafood3.jpg",
            "https://d3c5012dwkvoyc.cloudfront.net/food/22946089-2226-45e0-9c02-6159bb9db948food8.jpg",
            "https://d3c5012dwkvoyc.cloudfront.net/food/edb9bd71-ffd7-4871-8304-a20d428c3f0bcowsoup.webp",
            "https://d3c5012dwkvoyc.cloudfront.net/food/d6f11d9d-47d8-4670-89a9-91f53bc35b80kimchijeon.webp",
            "https://d3c5012dwkvoyc.cloudfront.net/food/b52b1833-7a93-46b2-b5b2-a6a4ef489bf1_kimchiBokumBap.webp",
            "https://d3c5012dwkvoyc.cloudfront.net/food/b921405c-a398-4390-bc0c-51c68ee152d5_baked-goods-1846460_1280.jpg",
            "https://d3c5012dwkvoyc.cloudfront.net/food/aa54e1c6-ed80-47de-abb2-6b9c1801bc08ChickenAssholeFries.webp",
            "https://d3c5012dwkvoyc.cloudfront.net/food/a18e4ed1-ce9a-4de7-ae19-bbe77bd916a5_breakfast-456351_1280.jpg"
    );

    int randomInt(int start, int end) {
        return (int) ((Math.random() * (end - start + 1)) + start);
    }

    long randomLong(int start, int end) {
        return (long) ((Math.random() * (end - start + 1)) + start);
    }

    @Test
    @DisplayName("registerCook")
    void registerCookTest() {
        for (int i = 0; i < names.size(); i++) {
            String name = names.get(i);
            String allergy = allergies.get(randomInt(0, allergies.size() - 1));
            String recipe = recipes.get(randomInt(0, recipes.size() - 1));
            String thumbnail = "https://d3c5012dwkvoyc.cloudfront.net/food/image" + i % 10 + " (Copy " + i / 10 + ").webp";
            String category = categories.get(randomInt(0, categories.size() - 1)).name();




            List<CookCreateReqDto.IngredientDto> ingredients = new ArrayList<>();
            for (int j = 0; j < randomInt(2, 5); j++) {
                CookCreateReqDto.IngredientDto ingredientDto = new CookCreateReqDto.IngredientDto(randomLong(1, 100), randomInt(1, 3));
                ingredients.add(ingredientDto);
            }

            CookCreateReqDto cookCreateReqDto = new CookCreateReqDto(name, allergy, recipe, category, thumbnail, ingredients);
            cookService.createCook(cookCreateReqDto);
        }

        // given

        // when

        // then

    }

}
