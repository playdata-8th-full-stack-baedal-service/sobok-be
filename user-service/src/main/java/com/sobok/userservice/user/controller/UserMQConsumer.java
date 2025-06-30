package com.sobok.userservice.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sobok.userservice.user.dto.request.UserSignupReqDto;
import com.sobok.userservice.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserMQConsumer {
    private final ObjectMapper objectMapper;
    private final UserService userService;

    @RabbitListener(queues = "user.queue")
    public void handleRecommendMessage(Message message) throws InterruptedException {
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();

        switch (routingKey) {
            case "auth.user.signup" :
                userSignUp(message.getBody());
                break;
        }
    }

    private void userSignUp(byte[] body) {
        try {
            log.info("MQ 사용자 회원가입 메시지 수신!");

            UserSignupReqDto reqDto = objectMapper.readValue(body, UserSignupReqDto.class);
            userService.signup(reqDto);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
