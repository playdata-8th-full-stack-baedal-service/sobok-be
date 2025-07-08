package com.sobok.userservice.user.entity;

import com.sobok.userservice.user.dto.request.UserAddressReqDto;
import com.sobok.userservice.user.dto.response.UserLocationResDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "user_address")
public class UserAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String roadFull;

    @Column(nullable = true)
    private String addrDetail;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = true)
    @Builder.Default
    private String active = "Y";

    public void editAddress(UserAddressReqDto addressDto, UserLocationResDto locationDto) {
        this.roadFull = addressDto.getRoadFull();
        this.addrDetail = addressDto.getAddrDetail();
        this.latitude = locationDto.getLatitude();
        this.longitude = locationDto.getLongitude();
    }

    public void editDetail(String addrDetail) {
        this.addrDetail = addrDetail;
    }

    public void convertActive(boolean active) {
        this.active = active ? "Y" : "N";
    }
}
