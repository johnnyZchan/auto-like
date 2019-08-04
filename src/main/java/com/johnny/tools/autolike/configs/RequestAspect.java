package com.johnny.tools.autolike.configs;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Aspect
@Component
@Slf4j
public class RequestAspect {

    @Pointcut("execution(public * com.johnny.tools.autolike.controllers.*.*(..))")
    public void log() {

    }

    @Before("log()")
    public void exBefore(JoinPoint joinPoint) {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = sra.getRequest();
        log.info(joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName() + " 方法执行开始...");
        Object[] args = joinPoint.getArgs();
        List<Object> logArgs = streamOf(args)
                .filter(arg -> (!(arg instanceof HttpServletRequest) && !(arg instanceof File) && !(arg instanceof MultipartFile) && !(arg instanceof HttpServletResponse)))
                .collect(Collectors.toList());
        String argStr = JSON.toJSONString(logArgs);
        log.info("参数：" + argStr);
    }

    @After("log()")
    public void exAfter(JoinPoint joinPoint) {
        log.info(joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName() + " 方法执行结束...");
    }

    @AfterReturning(returning = "result", pointcut = "log()")
    public void exAfterReturning(Object result) {
        log.info("返回值：" + JSON.toJSONString(result));
    }

    public <T> Stream<T> streamOf(T[] array) {
        return ArrayUtils.isEmpty(array) ? Stream.empty() : Arrays.asList(array).stream();
    }

}
