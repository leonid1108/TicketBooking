package com.application.ticketbooking.aop.pointcut;

import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Класс, содержащий Pointcut-выражения для перехвата методов в {@code BookingController}.
 * Используется в AOP-аспектах для логирования вызовов методов контроллера.
 */
@Component
public class BookingControllerPointcuts {

    @Pointcut("execution(* com.application.ticketbooking.controller.BookingController.bookTickets(..))")
    public void bookTickets() { }

    @Pointcut("execution(* com.application.ticketbooking.controller.BookingController.getAllBooking(..))")
    public void getAllBooking() { }
}
