package com.application.ticketbooking.aop.pointcut;

import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Класс, содержащий Pointcut-выражения для перехвата методов в {@code NotificationsLogController}.
 * Используется в AOP-аспектах для логирования вызовов методов контроллера.
 */
@Component
public class NotificationsLogControllerPointcuts {

    @Pointcut("execution(* com.application.ticketbooking.controller.NotificationsLogController.getAllNotifications(..))")
    public void getAllNotifications() { }

}


