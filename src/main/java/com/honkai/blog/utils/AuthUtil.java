
package com.honkai.blog.utils;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import com.honkai.blog.db.entity.User;
import com.honkai.blog.db.service.UserService;
import com.honkai.blog.exception.CustomException;

import cn.hutool.core.convert.NumberWithFormat;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import jakarta.annotation.Resource;


@Component
public class AuthUtil {

    private final static String JWT_KEY = "IMAXVOIDPANIC";

    public String generateToken(Integer userId) {
        Map<String, Object> payload = new HashMap<>(3);
        payload.put("userId", userId);
        return JWTUtil.createToken(payload, JWT_KEY.getBytes());
    }

    public HashMap<String, Integer> getPayload(String token) {
        if (!JWTUtil.verify(token, JWT_KEY.getBytes())) {
            throw new CustomException(401, "无效Token");
        }
        final JWT jwt = JWTUtil.parseToken(token);
        Object userId = jwt.getPayload("userId");
        if (userId == null) {
            throw new CustomException(401, "无效Token");
        }
        HashMap<String, Integer> payload = new HashMap<>(1);
        payload.put("userId", ((NumberWithFormat) userId).intValue());
        return payload;
    }

    public Integer getUserId() {
        String userIdStr = MDC.get("userId");
        return userIdStr == null ? null : Integer.valueOf(userIdStr);
    }

    public Integer getRole() {
        String roleStr = MDC.get("role");
        return roleStr == null ? null : Integer.valueOf(roleStr);
    }

    @Resource
    UserService userService;

    public User getUser() {
        Integer userId = getUserId();
        return userId == null ? null : userService.getById(userId);
    }
}
