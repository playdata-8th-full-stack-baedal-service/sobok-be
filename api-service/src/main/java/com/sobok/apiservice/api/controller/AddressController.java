package com.sobok.apiservice.api.controller;

import com.sobok.apiservice.api.dto.address.LocationResDto;
import com.sobok.apiservice.api.service.address.ConvertAddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/address")
@Slf4j
public class AddressController {

    private final ConvertAddressService convertAddressService;

    @GetMapping("/convert-addr")
    public LocationResDto convertAddress(@RequestParam String roadFull) {
        return convertAddressService.getLocation(roadFull);
    }
}
