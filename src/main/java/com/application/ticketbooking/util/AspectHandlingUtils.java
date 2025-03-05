package com.application.ticketbooking.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.springframework.util.StopWatch;

public class AspectHandlingUtils {

    public static Object handleRequest(Logger logger, ProceedingJoinPoint joinPoint, String action) throws Throwable{
        String methodName = joinPoint.getSignature().toShortString();
        logMethodStart(logger, action, joinPoint);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            Object result = joinPoint.proceed();
            stopWatch.stop();
            logMethodSuccess(logger, action, methodName, stopWatch.getTotalTimeMillis());
            return result;
        } catch (Throwable ex) {
            logMethodError(logger, action, methodName, ex);
            throw ex;
        } finally {
            if (stopWatch.isRunning()) {
                stopWatch.stop();
            }
            logMethodCompletion(logger, action, methodName, stopWatch.getTotalTimeMillis());
        }
    }

    public static void logMethodStart(Logger logger, String action, ProceedingJoinPoint joinPoint) {
        logger.info("Выполнение метода {} с аргументами: {}", action, joinPoint.getSignature().toShortString());
    }

    public static void logMethodSuccess(Logger logger, String action, String methodName, long duration) {
        logger.info("{}: Метод {} успешно выполнен за {} мс", action, methodName, duration);
    }

    public static void logMethodCompletion(Logger logger, String action, String methodName, long duration) {
        logger.info("{}: Метод {} завершен за {} мс", action, methodName, duration);
    }

    public static void logMethodError(Logger logger, String action, String methodName, Throwable ex) {
        logger.error("{}: Исключение в методе {}: {}", action, methodName, ex.getMessage(), ex);
    }
}
