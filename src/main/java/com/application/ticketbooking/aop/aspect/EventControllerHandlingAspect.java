package com.application.ticketbooking.aop.aspect;

import com.application.ticketbooking.util.AspectHandlingUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Аспект для логирования вызовов методов контроллера {@code EventController}.
 * Он перехватывает вызовы методов и логирует их выполнение.
 */
@Slf4j
@Aspect
@Component
public class EventControllerHandlingAspect {

    @Around("com.application.ticketbooking.aop.pointcut.EventControllerPointcuts.getAllEvents()")
    public Object getAllEvents(ProceedingJoinPoint joinPoint) throws Throwable {
        return AspectHandlingUtils.handleRequest(log, joinPoint, "Вызов сервиса для получения всех мероприятий");
    }

    @Around("com.application.ticketbooking.aop.pointcut.EventControllerPointcuts.getEventById()")
    public Object getEventById(ProceedingJoinPoint joinPoint) throws Throwable {
        return AspectHandlingUtils.handleRequest(log, joinPoint, "Вызов сервиса для получения мероприятия по id");
    }
    @Around("com.application.ticketbooking.aop.pointcut.EventControllerPointcuts.createEvent()")
    public Object createEvent(ProceedingJoinPoint joinPoint) throws Throwable {
        return AspectHandlingUtils.handleRequest(log, joinPoint, "Вызов сервиса для создания записи о мероприятии");
    }

    @Around("com.application.ticketbooking.aop.pointcut.EventControllerPointcuts.updateEvent()")
    public Object updateEvent(ProceedingJoinPoint joinPoint) throws Throwable {
        return AspectHandlingUtils.handleRequest(log, joinPoint, "Вызов сервиса для обновления информации о мероприятии");
    }
    @Around("com.application.ticketbooking.aop.pointcut.EventControllerPointcuts.deleteEvent()")
    public Object deleteEvent(ProceedingJoinPoint joinPoint) throws Throwable {
        return AspectHandlingUtils.handleRequest(log, joinPoint, "Вызов сервиса для удаления записи о мероприятии");
    }
}
