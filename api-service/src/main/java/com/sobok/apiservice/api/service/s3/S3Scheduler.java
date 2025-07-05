package com.sobok.apiservice.api.service.s3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3Scheduler {
    private final RedisTemplate<String, String> redisTemplate;

    @Scheduled(fixedRate = 60000)
    public void checkS3ImageValidation() {

    }
}
