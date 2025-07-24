package com.sobok.cookservice.cook.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sobok.cookservice.common.enums.CookCategory;
import com.sobok.cookservice.common.exception.CustomException;
import com.sobok.cookservice.cook.client.ApiServiceClient;
import com.sobok.cookservice.cook.client.PaymentFeignClient;
import com.sobok.cookservice.cook.dto.request.CookCreateReqDto;
import com.sobok.cookservice.cook.dto.response.*;
import com.sobok.cookservice.cook.entity.*;
import com.sobok.cookservice.cook.repository.CombinationRepository;
import com.sobok.cookservice.cook.repository.CookQueryRepository;
import com.sobok.cookservice.cook.repository.CookRepository;
import com.sobok.cookservice.cook.repository.IngredientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.sobok.cookservice.cook.entity.QCombination.*;
import static com.sobok.cookservice.cook.entity.QCook.cook;
import static com.sobok.cookservice.cook.entity.QIngredient.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class CookService {


    private final CookRepository cookRepository;
    private final CombinationRepository combinationRepository;
    private final IngredientRepository ingredientRepository;
    private final JPAQueryFactory factory;
    private final PaymentFeignClient paymentFeignClient;
    private final ApiServiceClient apiServiceClient;
    private final CookQueryRepository  cookQueryRepository;

    @Transactional
    public CookCreateResDto createCook(CookCreateReqDto dto) {

        // 레시피 이름 중복 검증
        cookRepository.findByName(dto.getName())
                .ifPresent(cook -> {
                    throw new CustomException("이미 존재하는 요리 이름입니다.", HttpStatus.BAD_REQUEST);
                });

//
//        // 사진 등록
//        String photoUrl;
//        try {
//            photoUrl = apiServiceClient.registerImg(dto.getThumbnailUrl());
//        } catch (Exception e) {
//            log.error("사진 등록 실패", e);
//            photoUrl = dto.getThumbnailUrl();
//        }

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
                .category(CookCategory.valueOf(dto.getCategory().toUpperCase()))
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
        builder.and(cook.active.eq("Y"));
        try {
            if (keyword.startsWith("category:")) { // 카테고리 조회
                // 카테고리 뽑기
                String category = keyword.replace("category:", "").toUpperCase();
                log.info(category);
                CookCategory categoryEnum = CookCategory.valueOf(category);

                builder.and(cook.category.eq(categoryEnum));
            } else if (!keyword.isBlank()) { // 키워드 조회
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
                                cook.thumbnail,
                                cook.active
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


    // 장바구니용 조회
    public CookDetailResDto getCookDetail(Long cookId) {
        // 요리 있는지부터 검증 없으면 예외
        Cook cook = cookRepository.findById(cookId)
                .orElseThrow(() -> new CustomException("해당 요리가 존재하지 않습니다.", HttpStatus.NOT_FOUND));

        // cook 테이블에 해당되는 식재료 찾기
        List<Combination> combinations = combinationRepository.findByCookId(cookId);

        // 각 조합에서 식재료 정보를 추출하여 dto로 변환
        List<CookIngredientResDto> ingredients = combinations.stream().map(comb -> {
            Ingredient ingre = comb.getIngredient(); // 연관된 식재료

            // 식재료가 없거나 존재하지 않으면 예외 던짐
            if (ingre == null) {
                ingre = ingredientRepository.findById(comb.getIngreId())
                        .orElseThrow(() -> new CustomException("식재료가 존재하지 않습니다: id=" + comb.getIngreId(), HttpStatus.NOT_FOUND));
            }

            return CookIngredientResDto.builder()
                    .ingredientId(ingre.getId())
                    .ingreName(ingre.getIngreName())
                    .unitQuantity(comb.getUnitQuantity())
                    .unit(ingre.getUnit())
                    .price(ingre.getPrice())
                    .origin(ingre.getOrigin())
                    .build();
        }).toList();

        return CookDetailResDto.builder()
                .cookId(cook.getId())
                .name(cook.getName())
                .thumbnail(cook.getThumbnail())
                .ingredients(ingredients)
                .build();
    }


    public boolean checkCook(Long cookId) {
        log.info("컨트롤러 통과");
        boolean exists = cookRepository.existsByIdAndActive(cookId, "Y");
        log.info(exists ? "존재" : "없음");
        return exists;
    }

    public List<UserBookmarkResDto> findCookById(List<Long> cookIds) {

        List<Cook> cooks = cookRepository.findAllByIdInAndActive(cookIds, "Y");

        List<UserBookmarkResDto> collect = cooks.stream()
                .map(cook -> new UserBookmarkResDto(
                        cook.getId(),
                        cook.getName(),
                        cook.getThumbnail()
                ))
                .toList();

        log.info("collect: " + collect);

        return collect;
    }

    //주문 내역 조회용
    public List<CookDetailResDto> getCookDetailList(List<Long> cookIds) {
        log.info("cookIds: " + cookIds);

        // 요리 있는지부터 검증 없으면 예외
        List<Cook> cooksList = new ArrayList<>();

        for (Long id : cookIds) {
            Cook cook = cookRepository.findById(id)
                    .orElseThrow(() -> new CustomException("ID " + id + "에 해당하는 요리가 존재하지 않습니다.", HttpStatus.NOT_FOUND));
            cooksList.add(cook);
        }

        log.info("cooksList: " + cooksList);

        return cooksList.stream()
                .map(cook -> CookDetailResDto.builder()
                        .cookId(cook.getId())
                        .name(cook.getName())
                        .thumbnail(cook.getThumbnail())
                        .build())
                .toList();
    }

    /**
     * 요리이름 조회용 (주문 전체 조회)
     */
    public List<CookNameResDto> getCookNamesByIds(List<Long> cookIds) {
        return cookRepository.findByIdIn(cookIds).stream()
                .map(cook -> new CookNameResDto(cook.getId(), cook.getName()))
                .collect(Collectors.toList());
    }

    /**
     * 게시글 등록(요리 조회)
     */
    public String getCookNameById(Long cookId) {
        Cook cook = cookRepository.findById(cookId)
                .orElseThrow(() -> new CustomException("해당 요리가 존재하지 않습니다.", HttpStatus.NOT_FOUND));
        return cook.getName();
    }

    /**
     * 요리 단건 조회
     */
    public CookIndividualResDto getCookById(Long cookId) {
        log.info("요리 단건 조회 시작 | cookId: " + cookId);

        List<Tuple> tuple = factory.select(
                        cook.id,
                        cook.name,
                        cook.allergy,
                        cook.category,
                        cook.recipe,
                        cook.thumbnail,
                        ingredient.id,
                        ingredient.ingreName,
                        ingredient.price,
                        ingredient.unit,
                        combination.unitQuantity
                )
                .from(cook)
                .where(cook.id.eq(cookId).and(cook.active.eq("Y")))
                .join(combination)
                .on(combination.cookId.eq(cook.id))
                .join(ingredient)
                .on(ingredient.id.eq(combination.ingreId))
                .fetch();

        if (tuple.isEmpty()) {
            log.error("일치하는 요리가 존재하지 않습니다.");
            throw new CustomException("일치하는 요리가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
        }

        // 공통 Cook 정보는 첫 줄에서만 꺼내면 됨
        Tuple first = tuple.get(0);

        CookIndividualResDto.CookIndividualResDtoBuilder builder = CookIndividualResDto.builder()
                .cookId(first.get(cook.id))
                .cookName(first.get(cook.name))
                .allergy(first.get(cook.allergy))
                .category(first.get(cook.category).toString())
                .recipe(first.get(cook.recipe))
                .thumbnail(first.get(cook.thumbnail));

        List<CookIndividualResDto.IngredientAll> ingredientList = tuple.stream()
                .map(t -> CookIndividualResDto.IngredientAll.builder()
                        .ingredientId(t.get(ingredient.id))
                        .ingredientName(t.get(ingredient.ingreName))
                        .price(t.get(ingredient.price))
                        .unit(Integer.parseInt(t.get(ingredient.unit)))
                        .unitQuantity(t.get(combination.unitQuantity))
                        .build())
                .collect(Collectors.toList());

        return builder.ingredientList(ingredientList).build();

    }

    /**
     * 특정 요리에 포함된 기본 식재료 목록을 조회
     */
    public List<CookWithIngredientResDto> getBaseIngredients(Long cookId) {
        List<Combination> combinations = combinationRepository.findByCookId(cookId);
        return combinations.stream().map(comb -> {
            Ingredient ingre = comb.getIngredient();

            if (ingre == null) {
                throw new CustomException("식재료가 존재하지 않습니다. id=" + comb.getIngreId(), HttpStatus.NOT_FOUND);
            }

            return CookWithIngredientResDto.builder()
                    .ingredientId(ingre.getId())
                    .ingredientName(ingre.getIngreName())
                    .unit(String.valueOf(ingre.getUnit()))
                    .quantity(comb.getUnitQuantity())
                    .price(ingre.getPrice() != null ? ingre.getPrice() : 0)
                    .origin(ingre.getOrigin() != null ? ingre.getOrigin() : "정보 없음")
                    .isDefault(true)
                    .build();
        }).toList();
    }

    /**
     * 식재료 ID로 식재료 상세 정보를 조회
     */
    public IngredientInfoResDto getIngredientInfo(Long ingreId) {
        Ingredient ingre = ingredientRepository.findById(ingreId)
                .orElseThrow(() -> new CustomException("식재료가 존재하지 않습니다. id=" + ingreId, HttpStatus.NOT_FOUND));

        return IngredientInfoResDto.builder()
                .ingredientId(ingre.getId())
                .ingredientName(ingre.getIngreName())
                .unit(String.valueOf(ingre.getUnit()))
                .price(ingre.getPrice() != null ? ingre.getPrice() : 0)
                .origin(ingre.getOrigin() != null ? ingre.getOrigin() : "정보 없음")
                .build();
    }

    /**
     * 한달 주문량 기준 요리 페이지 조회
     */
    @Cacheable(value = "popularCooks", key = "'page:' + #page + ':size:' + #size")
    public PagedResponse<PopularCookResDto> getPopularCooks(int page, int size) {
        ResponseEntity<List<CookOrderCountDto>> popularCooks;
        try {
            popularCooks = paymentFeignClient.getPopularCookIds(page, size);
            if (popularCooks.getBody() == null || popularCooks.getBody().isEmpty()) {
                throw new CustomException("주문 데이터가 없습니다.", HttpStatus.NO_CONTENT);
            }
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("payment-service 통신 실패", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        List<Long> cookIds = popularCooks.getBody().stream()
                .map(CookOrderCountDto::getCookId)
                .collect(Collectors.toList());

        List<Cook> cooks = cookRepository.findByIdIn(cookIds);
        Map<Long, Cook> cookMap = cooks.stream()
                .collect(Collectors.toMap(Cook::getId, c -> c));

        List<PopularCookResDto> result = popularCooks.getBody().stream()
                .map(dto -> {
                    Cook cook = cookMap.get(dto.getCookId());
                    if (cook == null) {
                        throw new CustomException("요리 정보가 없습니다. id=" + dto.getCookId(), HttpStatus.NOT_FOUND);
                    }
                    return PopularCookResDto.builder()
                            .cookId(cook.getId())
                            .cookName(cook.getName())
                            .thumbnail(cook.getThumbnail())
                            .orderCount(dto.getOrderCount())
                            .build();
                })
                .toList();

        return PagedResponse.<PopularCookResDto>builder()
                .content(result)
                .page(page)
                .size(size)
                .totalElements(result.size())
                .totalPages(1)
                .first(page == 0)
                .last(true)
                .build();
    }

    /**
     * 스케줄러
     */
    @Scheduled(cron = "0 0 0 * * *") // 매일 자정에 실행
    public void preloadPopularCookCache() {
        getPopularCooks(0, 10); // 0페이지 10개짜리 캐싱
    }



    public List<MonthlyHotCookDto> getMonthlyHotCooks(int pageNo, int numOfRows) {
        CartMonthlyHotDto resDto;
        try {
            resDto = paymentFeignClient.getMonthlyHotCooks(pageNo, numOfRows);
            if (resDto == null) {
                throw new CustomException("<UNK> <UNK> <UNK>.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("payment-service <UNK> <UNK> <UNK>.", HttpStatus.INTERNAL_SERVER_ERROR);
        }


        List<CartMonthlyHotDto.MonthlyHot> monthlyHot = resDto.getMonthlyHot();

        // CookId List 만들기
        List<Long> cookIdList = getCookIdList(monthlyHot);


        // 주문량 순으로는 페이징이 불가능한 경우
        if (!resDto.isAvailable()) {
            int offset = Math.max((pageNo - 1) * numOfRows - resDto.getCount(), 0);
            int limit = Math.min(pageNo * numOfRows - resDto.getCount(), numOfRows);

            // 주문하지 않은 CookId 조회
            List<CartMonthlyHotDto.MonthlyHot> notOrderedCookIdList
                    = cookQueryRepository.getNotOrderCookIdList(cookIdList)
                    .stream()
                    .skip(offset)
                    .limit(limit)
                    .toList();

            List<CartMonthlyHotDto.MonthlyHot> limitHot
                    = numOfRows == limit ?
                    new ArrayList<>() :
                    monthlyHot.stream()
                    .skip((long) (pageNo - 1) * numOfRows)
                    .limit(numOfRows - limit)
                    .collect(Collectors.toCollection(ArrayList::new));

            log.error("limitHot : {}", limitHot);

            limitHot.addAll(notOrderedCookIdList);
            log.error("notOrderedCookIdList : {}", notOrderedCookIdList);
            log.error("limit : {}", limitHot);
            resDto.setMonthlyHot(limitHot);
            log.error("resDto.getMonthlyHot() : {}", resDto.getMonthlyHot());
        }

       return getMonthlyHotCookList(resDto);
    }

    private List<MonthlyHotCookDto> getMonthlyHotCookList(CartMonthlyHotDto resDto) {
        List<MonthlyHotCookDto> result = new ArrayList<>();
        List<CartMonthlyHotDto.MonthlyHot> monthlyHot = resDto.getMonthlyHot();
        List<Long> cookIdList = getCookIdList(monthlyHot);

        // 인덱스 맵핑
        Map<Long, Integer> cookIdMap = new HashMap<>();
        for (int i = 0; i < cookIdList.size(); i++) cookIdMap.put(cookIdList.get(i), i);

        // CookId List 기반 Cook List 정렬
        List<Cook> sortedCookList = cookRepository.findByIdIn(cookIdList)
                .stream()
                .sorted(Comparator.comparing(key -> cookIdMap.get(key.getId())))
                .toList();

        // 응답 객체 생성
        for (int i = 0; i < sortedCookList.size(); i++) {
            result.add(new MonthlyHotCookDto(sortedCookList.get(i), monthlyHot.get(i)));
        }

        return result;
    }

    private static List<Long> getCookIdList(List<CartMonthlyHotDto.MonthlyHot> monthlyHot) {
        // CookId List 만들기
        return monthlyHot
                .stream()
                .map(CartMonthlyHotDto.MonthlyHot::getCookId)
                .toList();
    }
}

