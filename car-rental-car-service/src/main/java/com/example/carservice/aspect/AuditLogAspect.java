package com.example.carservice.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditLogAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditLogAspect.class);

    @Around("execution(* com.example.carservice.controller.*.*(..))")
    public Object logApiCall(ProceedingJoinPoint joinPoint) throws Throwable {

        String method = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        long start = System.currentTimeMillis();

        // Récupérer le username depuis Spring Security
        String user = "anonymous";
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated()) {
            user = auth.getName();
        }

        log.info(">>> API CALL | user={} | {}.{}() | started",
                user, className, method);

        try {
            Object result = joinPoint.proceed();

            long duration = System.currentTimeMillis() - start;

            log.info("<<< API DONE | user={} | {}.{}() | {}ms | SUCCESS",
                    user, className, method, duration);

            return result;

        } catch (Exception e) {

            long duration = System.currentTimeMillis() - start;

            log.error("<<< API FAIL | user={} | {}.{}() | {}ms | ERROR: {}",
                    user, className, method, duration, e.getMessage());

            throw e;
        }
    }
}