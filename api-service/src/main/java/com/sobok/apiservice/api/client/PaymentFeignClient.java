package com.sobok.apiservice.api.client;

import com.sobok.apiservice.common.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "payment-service", configuration = FeignConfig.class)
public interface PaymentFeignClient {
}
