package com.honkai.blog.aspect;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.honkai.blog.annotation.RequireAuth;
import com.honkai.blog.bean.Result;
import com.honkai.blog.db.entity.User;
import com.honkai.blog.db.service.UserService;
import com.honkai.blog.utils.AuthUtil;
import com.honkai.blog.utils.Jackson;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class WebLogAspect {

    @Pointcut(value = "execution (* com.honkai.blog.controller..*.*(..))")
    private void pointCut4Controller() {
    }

    @Resource
    AuthUtil authUtil;

    @Resource
    UserService userService;

    @Around(value = "pointCut4Controller()")
    private Object method(ProceedingJoinPoint pjp) throws Throwable {
        Signature signature = pjp.getSignature();
        String className = signature.getDeclaringTypeName();
        String methodName = signature.getName();
        Object[] args = pjp.getArgs();

        log.info("调用方法:{}.{}", className, methodName);
        log.info("请求参数:{}", Arrays.toString(args));

        Class<?> targetClass = pjp.getTarget().getClass();
        if (targetClass.getAnnotation(RequireAuth.class) != null) {

            HttpServletRequest request = ((ServletRequestAttributes) Objects
                    .requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
            String token = request.getHeader("token");
            if (token == null) {
                return Result.fail(401, "请先登录", null);
            } else {
                HashMap<String, Integer> payload = null;
                try {
                    payload = authUtil.getPayload(token);
                } catch (Exception e) {
                    return Result.fail(401, "请先登录", null);
                }
                if (payload == null || payload.isEmpty()) {
                    return Result.fail(401, "请先登录", null);
                }
                User user = userService.getById(payload.get("userId"));
                if (user == null) {
                    return Result.fail(401, "请先登录", null);
                }
                MDC.put("userId", payload.get("userId").toString());
            }
        }
        long startTime = System.currentTimeMillis();
        Object result = pjp.proceed();
        long time = System.currentTimeMillis() - startTime;
        log.info("方法调用结束----->\n返回值:{}\n调用耗时:{}ms", Jackson.toJsonString(result), time);
        return result;
    }
}
