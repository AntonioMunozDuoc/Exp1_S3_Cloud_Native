package com.duoc.guiaservice.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

// Aspecto para logging de métodos en el servicio de inscripciones
@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger =
            LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* com.duoc.evaluationservice.service..*(..))")
    public void before(JoinPoint jp) {
        logger.info("[EVAL - INICIO] {}", jp.getSignature());
    }

    @AfterReturning("execution(* com.duoc.evaluationservice.service..*(..))")
    public void success(JoinPoint jp) {
        logger.info("[EVAL - OK] {}", jp.getSignature());
    }

    @AfterThrowing(pointcut = "execution(* com.duoc.evaluationservice.service..*(..))", throwing = "ex")
    public void error(JoinPoint jp, Exception ex) {
        logger.error("[EVAL - ERROR] {}", jp.getSignature());
        logger.error("Mensaje: {}", ex.getMessage());
    }
}