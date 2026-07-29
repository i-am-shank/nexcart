package com.springProjects.onlineStore.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class ApiExecutionAspect {
    private static final Logger logger = LoggerFactory.getLogger(ApiExecutionAspect.class);

    @Around("execution(* com.springProjects.onlineStore..controller..*(..))")
    public Object measureApiExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long executionStartTime = 0, executionEndTime = 0;
        Object result = null;
        try {
            // Unix time in milliseconds starting from 1 Jan 1970 - can affect if System clock / time zone changes
            // executionStartTime = System.currentTimeMillis();

            // This is like time counter (not System clock) - will not be affected by any external factors
            // time in nanoseconds  -  not milliseconds
            executionStartTime = System.nanoTime();

            // Execute actual method  (might trigger exception also .. then execution won't reach back here)
            result = joinPoint.proceed();
        } finally {
            // So finally block  -  Even if exception happened while method execution, finally block executes
            executionEndTime = System.nanoTime();

            // totalExecutionTime  :  converted from nanoSeconds to milliSeconds
            long totalExecutionTime = ((executionEndTime - executionStartTime) / (long) Math.pow(10, 6)) ;
            if(totalExecutionTime < 200) {
                logger.info("{} API execution time : {} ms", joinPoint.getSignature().getName(), totalExecutionTime);
            } else if(totalExecutionTime < 1000) {
                logger.warn("{} API execution time : {} ms", joinPoint.getSignature().getName(), totalExecutionTime);
            } else {
                logger.error("{} API execution time : {} ms", joinPoint.getSignature().getName(), totalExecutionTime);
            }
        }
        return result;
    }
}
