package com.sobok.deliveryservice.common.aspects;

import com.sobok.deliveryservice.common.dto.CommonResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    // 포인트컷: 어떤 메서드에 적용할지 정의 (@Service 아래 모든 메서드)
    @Pointcut("execution(* com.sobok.deliveryservice.delivery.service..*(..))")
    public void serviceMethods() {
    }

    @Pointcut("execution(* com.sobok.deliveryservice.delivery.controller..*(..))")
    public void controllerMethods() {
    }

    @Before("controllerMethods()")
    public void logRequest(JoinPoint joinPoint) {
        // 현재 HTTP 요청 객체 가져오기
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            log.warn("RequestAttributes is null");
            return;
        }
        HttpServletRequest request = attributes.getRequest();
        String path = request.getRequestURI(); // 요청 URL 경로
        String method = request.getMethod(); // GET, POST 등 HTTP 메서드
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        log.info("[요청] HTTP: {} {} {}() called with args: {}", method, path, methodName, Arrays.toString(args));
    }

    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
    public void logResponse(JoinPoint joinPoint, Object result) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        if (result instanceof ResponseEntity) {
            Object body = ((ResponseEntity<?>) result).getBody();

            if (body instanceof CommonResponse) {
                String message = ((CommonResponse<?>) body).getMessage();
                log.info("[응답 메시지] {}.{}() -> message: {}", className, methodName, message);
            } else {
                log.info("[응답] {}.{}() -> Non-ApiResponse body: {}", className, methodName, body);
            }
        } else {
            log.info("[응답] {}.{}() -> Non-ResponseEntity result: {}", className, methodName, result);
        }
    }

    @AfterThrowing(pointcut = "controllerMethods()", throwing = "e")
    public void logException(JoinPoint joinPoint, Throwable e) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        log.error("[EXCEPTION] {}.{}() threw: {}", className, methodName, e.getMessage(), e);
    }


    /**
     * Around : 대상 “메서드” 실행 전, 후 또는 예외 발생 시에 Advice를 실행합니다.
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("serviceMethods()") // 서비스 계층 전체 적용
    public Object logAndMeasure(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.info("[START] {}.{}() with args: {}", className, methodName, Arrays.toString(args));

        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed(); // 실제 메서드 실행
            long duration = System.currentTimeMillis() - startTime;

            log.info("[SUCCESS] {}.{}() returned: {}", className, methodName, result);
            log.info("[ExecutionTime] {}.{}() took {} ms", className, methodName, duration);

            return result;
        } catch (Throwable e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("[EXCEPTION] {}.{}() threw: {}", className, methodName, e.getMessage());
            log.info("[ExecutionTime] {}.{}() took {} ms (with exception)", className, methodName, duration);
            throw e;
        }
    }
}
