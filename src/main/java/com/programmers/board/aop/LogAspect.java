package com.programmers.board.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;

import java.util.Arrays;

@Slf4j
@Aspect
public class LogAspect {
    @Pointcut("execution(* com.programmers.board.service..*(..))")
    private void logService() {}

    @Pointcut("execution(* com.programmers.board.controller.GlobalControllerAdvice.exHandle(..))")
    private void logUnexpectedEx() {}

    @AfterThrowing(value = "logService()", throwing = "ex")
    public void doLogExpectedServiceEx(JoinPoint joinPoint, Exception ex) {
        log.warn("[EX Point] {}", joinPoint.getSignature());
        log.warn("[EX] Type = {}, Message = {}", ex.getClass().getSimpleName(), ex.getMessage());
    }

    @Before("logUnexpectedEx()")
    public void doLogUnexpectedServiceEx(JoinPoint joinPoint) {
        Arrays.stream(joinPoint.getArgs())
                .filter(o -> o instanceof Exception)
                .forEach(o -> {
                    Exception ex = (Exception) o;
                    log.error("[EX] Unexpected exception occurred", ex);
                });
    }
}