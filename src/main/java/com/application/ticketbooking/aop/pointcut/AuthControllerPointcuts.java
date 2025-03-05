package com.application.ticketbooking.aop.pointcut;

import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Класс, содержащий Pointcut-выражения для перехвата методов в {@code AuthController}.
 * Используется в AOP-аспектах для логирования вызовов методов контроллера.
 */
@Component
public class AuthControllerPointcuts {

    @Pointcut("execution(* com.application.ticketbooking.controller.AuthController.registerUser(..))")
    public void registerUser() { }

    @Pointcut("execution(* com.application.ticketbooking.controller.AuthController.loginUser(..))")
    public void loginUser() { }
}
