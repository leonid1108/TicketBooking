package com.application.ticketbooking.aop.pointcut;

import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Класс, содержащий Pointcut-выражения для перехвата методов в {@code EventController}.
 * Используется в AOP-аспектах для логирования вызовов методов контроллера.
 */
@Component
public class EventControllerPointcuts {

    @Pointcut("execution(* com.application.ticketbooking.controller.EventController.getAllEvents(..))")
    public void getAllEvents() { }

    @Pointcut("execution(* com.application.ticketbooking.controller.EventController.getEventById(..))")
    public void getEventById() { }

    @Pointcut("execution(* com.application.ticketbooking.controller.EventController.createEvent(..))")
    public void createEvent() { }

    @Pointcut("execution(* com.application.ticketbooking.controller.EventController.updateEvent(..))")
    public void updateEvent() { }

    @Pointcut("execution(* com.application.ticketbooking.controller.EventController.deleteEvent(..))")
    public void deleteEvent() { }
}
