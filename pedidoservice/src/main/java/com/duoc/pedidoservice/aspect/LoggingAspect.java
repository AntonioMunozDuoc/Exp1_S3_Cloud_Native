package com.duoc.pedidoservice.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

//  Aspecto encargado de registrar logs de las operaciones realizadas en el servicio.
@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger =
            LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* com.duoc.pedidoservice.service..*(..))")
    public void before(JoinPoint jp) {
        logger.info("[COURSE - INICIO] {}", jp.getSignature());
    }

    @AfterReturning("execution(* com.duoc.pedidoservice.service..*(..))")
    public void success(JoinPoint jp) {
        logger.info("[COURSE - OK] {}", jp.getSignature());
    }

    @AfterThrowing(pointcut = "execution(* com.duoc.pedidoservice.service..*(..))", throwing = "ex")
    public void error(JoinPoint jp, Exception ex) {
        logger.error("[COURSE - ERROR] {}", jp.getSignature());
        logger.error("Mensaje: {}", ex.getMessage());
    }
}