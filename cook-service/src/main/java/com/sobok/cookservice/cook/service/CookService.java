package com.sobok.cookservice.cook.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sobok.cookservice.common.enums.CookCategory;
import com.sobok.cookservice.common.exception.CustomException;
import com.sobok.cookservice.cook.dto.request.CookCreateReqDto;
import com.sobok.cookservice.cook.dto.response.CookCreateResDto;
import com.sobok.cookservice.cook.dto.response.CookResDto;
import com.sobok.cookservice.cook.entity.Combination;
import com.sobok.cookservice.cook.entity.Cook;
import com.sobok.cookservice.cook.entity.Ingredient;
import com.sobok.cookservice.cook.entity.QCook;
import com.sobok.cookservice.cook.repository.CombinationRepository;
import com.sobok.cookservice.cook.repository.CookRepository;
import com.sobok.cookservice.cook.repository.IngredientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.sobok.cookservice.cook.entity.QCook.cook;

@Service
@Slf4j
@RequiredArgsConstructor
public class CookService {


    private final CookRepository cookRepository;
    private final CombinationRepository combinationRepository;
    private final IngredientRepository ingredientRepository;
    private final JPAQueryFactory factory;

    @Transactional
    public CookCreateResDto createCook(CookCreateReqDto dto) {

        // 레시피 이름 중복 검증
        cookRepository.findByName(dto.getName())
                .ifPresent(cook -> {
                    throw new CustomException("이미 존재하는 요리 이름입니다.", HttpStatus.BAD_REQUEST);
                });

        // 레시피 썸네일 중복 검증
        cookRepository.findByThumbnail(dto.getThumbnailUrl())
                .ifPresent(cook -> {
                    throw new CustomException("이미 사용 중인 썸네일입니다.", HttpStatus.BAD_REQUEST);
                });

        // 요리 저장
        Cook cook = Cook.builder()
                .name(dto.getName())
                .allergy(dto.getAllergy())
                .recipe(dto.getRecipe())
                .category(dto.getCategory())
                .thumbnail(dto.getThumbnailUrl())
                .build();

        cookRepository.save(cook); // DB 저장

        // 식재료 조합 저장
        List<Combination> combinations = dto.getIngredients().stream()
                .map(ingredientDto -> {
                    Ingredient ingredient = ingredientRepository.findById(ingredientDto.getIngredientId())
                            .orElseThrow(() -> new CustomException("해당 식재료가 존재하지 않습니다: id=" + ingredientDto.getIngredientId(),
                                    HttpStatus.BAD_REQUEST));

                    return Combination.builder()
                            .cookId(cook.getId())
                            .ingreId(ingredient.getId())
                            .ingredient(ingredient)
                            .unitQuantity(ingredientDto.getUnitQuantity())
                            .build();
                })
                .toList();

        combinationRepository.saveAll(combinations);

        // 등록된 요리 ID 반환
        return new CookCreateResDto(cook.getId());
    }


    public List<CookResDto> getCook(Long pageNo, Long numOfRows) {
        // 전체 조회
        return searchCook("", pageNo, numOfRows);
    }

    public List<CookResDto> searchCook(String keyword, Long pageNo, Long numOfRows) {
        // offset은 0부터 시작
        long offset = (pageNo - 1) * numOfRows;

        log.info(keyword);

        // 조건 분기
        BooleanBuilder builder = new BooleanBuilder();
        try {
            if (keyword.startsWith("category:")) { // 카테고리 조회
                // 카테고리 뽑기
                String category = keyword.replace("category:", "").toUpperCase();
                log.info(category);
                CookCategory categoryEnum = CookCategory.valueOf(category);

                builder.and(cook.category.eq(categoryEnum));
            } else if(!keyword.isBlank()) { // 전체 조회
                builder.and(cook.name.contains(keyword));
            }
        } catch (IllegalArgumentException e) {
            throw new CustomException("잘못된 카테고리 입력입니다.", HttpStatus.BAD_REQUEST);
        }


        return factory.select(
                        Projections.fields(
                                CookResDto.class,
                                cook.id,
                                cook.name,
                                cook.allergy,
                                cook.recipe,
                                cook.category,
                                cook.thumbnail
                        )
                )
                .from(cook)
                .where(builder)
                .offset(offset)
                .orderBy(cook.updatedAt.desc())
                .limit(numOfRows)
                .fetch();
    }

    public List<CookResDto> getCookByCategory(String category, Long pageNo, Long numOfRows) {
        // 카테고리 조회
        return searchCook("category:" + category, pageNo, numOfRows);
    }
}
