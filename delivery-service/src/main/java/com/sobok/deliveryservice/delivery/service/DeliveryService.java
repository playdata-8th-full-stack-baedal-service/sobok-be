package com.sobok.deliveryservice.delivery.service;

import com.sobok.deliveryservice.common.exception.CustomException;
import com.sobok.deliveryservice.delivery.dto.request.RiderReqDto;
import com.sobok.deliveryservice.delivery.dto.response.RiderResDto;
import com.sobok.deliveryservice.delivery.entity.Rider;
import com.sobok.deliveryservice.delivery.repository.RiderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DeliveryService {

    private final RiderRepository riderRepository;

    public RiderResDto riderCreate(RiderReqDto dto) {

        if (riderRepository.existsByPhone(dto.getPhone())) {
            throw new CustomException("이미 사용 중인 전화번호입니다.", HttpStatus.BAD_REQUEST);
        }
        if (riderRepository.existsByPermissionNumber(dto.getPermissionNumber())) {
            throw new CustomException("이미 사용 중인 면허번호입니다.", HttpStatus.BAD_REQUEST);
        }

        Rider rider = Rider.builder()
                .authId(dto.getAuthId())
                .name(dto.getName())
                .phone(dto.getPhone())
                .permissionNumber(dto.getPermissionNumber())
                .build();

        Rider saved = riderRepository.save(rider);

        return RiderResDto.builder()
                .id(saved.getId())
                .authId(saved.getAuthId())
                .name(saved.getName())
                .phone(saved.getPhone())
                .permissionNumber(saved.getPermissionNumber())
                .build();
    }
}
