package com.application.ticketbooking.aop.aspect;

import com.application.ticketbooking.util.AspectHandlingUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Аспект для логирования вызовов методов контроллера {@code BookingController}.
 * Он перехватывает вызовы методов и логирует их выполнение.
 */
@Slf4j
@Aspect
@Component
public class BookingControllerHandlingAspect {

    @Around("com.application.ticketbooking.aop.pointcut.BookingControllerPointcuts.bookTickets()")
    public Object bookTickets(ProceedingJoinPoint joinPoint) throws Throwable {
        return AspectHandlingUtils.handleRequest(log, joinPoint, "Вызов сервиса для бронирования мест на мероприятие");
    }

    @Around("com.application.ticketbooking.aop.pointcut.BookingControllerPointcuts.getAllBooking()")
    public Object getAllBooking(ProceedingJoinPoint joinPoint) throws Throwable {
        return AspectHandlingUtils.handleRequest(log, joinPoint, "Вызов сервиса для получения списка всех мероприятий");
    }

}
