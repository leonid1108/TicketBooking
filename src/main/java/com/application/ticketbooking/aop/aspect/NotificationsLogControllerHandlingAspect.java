package com.application.ticketbooking.aop.aspect;

import com.application.ticketbooking.util.AspectHandlingUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Аспект для логирования вызовов методов контроллера {@code NotificationsLogController}.
 * Он перехватывает вызовы методов и логирует их выполнение.
 */
@Slf4j
@Aspect
@Component
public class NotificationsLogControllerHandlingAspect {

    @Around("com.application.ticketbooking.aop.pointcut.NotificationsLogControllerPointcuts.getAllNotifications()")
    public Object getAllNotifications(ProceedingJoinPoint joinPoint) throws Throwable {
        return AspectHandlingUtils.handleRequest(log, joinPoint, "Вызов сервиса для получения всех уведомлений");
    }
}
