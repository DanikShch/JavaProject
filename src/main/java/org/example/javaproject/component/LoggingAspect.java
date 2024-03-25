package org.example.javaproject.component;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* org.example.javaproject.service.*.*(..)) || execution(* org.example.javaproject.controller.*.*(..))")
    public void logBeforeMethodExecution(JoinPoint joinPoint) {
        LOGGER.info("Method execution started: {}", joinPoint.getSignature().toString());
    }

    @AfterReturning(pointcut = "execution(* org.example.javaproject.service.*.*(..)) "
            + "|| execution(* org.example.javaproject.controller.*.*(..))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        LOGGER.info("Method execution completed: {}. Result: {}", joinPoint.getSignature().toString(), result);
    }

    @AfterThrowing(pointcut = "execution(* org.example.javaproject.service.*.*(..)) "
            + "|| execution(* org.example.javaproject.controller.*.*(..))", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Exception ex) {
        LOGGER.info("Method execution failed: {}. Exception: {}", joinPoint.getSignature().toString(), ex.getMessage());
    }

}
