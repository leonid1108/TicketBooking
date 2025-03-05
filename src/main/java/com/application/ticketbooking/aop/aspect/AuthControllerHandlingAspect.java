package com.application.ticketbooking.aop.aspect;

import com.application.ticketbooking.util.AspectHandlingUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Аспект для логирования вызовов методов контроллера {@code AuthController}.
 * Он перехватывает вызовы методов и логирует их выполнение.
 */
@Slf4j
@Aspect
@Component
public class AuthControllerHandlingAspect {

    @Around("com.application.ticketbooking.aop.pointcut.AuthControllerPointcuts.registerUser()")
    public Object registerUser(ProceedingJoinPoint joinPoint) throws Throwable {
        return AspectHandlingUtils.handleRequest(log, joinPoint, "Вызов сервиса для регистрации пользователя в системе");
    }

    @Around("com.application.ticketbooking.aop.pointcut.AuthControllerPointcuts.loginUser()")
    public Object loginUser(ProceedingJoinPoint joinPoint) throws Throwable {
        return AspectHandlingUtils.handleRequest(log, joinPoint, "Вызов сервиса для аутентификации пользователя в системе");
    }
}
