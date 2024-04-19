
package com.honkai.blog.handler;

import com.honkai.blog.bean.Result;
import com.honkai.blog.exception.CustomException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class WebExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public Result<Object> customExceptionHandler(HttpServletRequest request, CustomException e) {
        log.error("已捕获自定义异常: RequestURI: {}, 异常信息: {}", request.getRequestURI(), e.getMsg());
        return Result.fail(e.getCode(), e.getMsg(), e.getData());
    }

    @ExceptionHandler(Exception.class)
    public Result<Object> exceptionHandler(HttpServletRequest request, Exception e) {
        log.error("已捕获未处理异常:RequestURL:{}, 异常信息:{}", request.getRequestURI(), e.getMessage());
        e.printStackTrace();
        return Result.fail(500, "服务器内部错误", null);
    }

}
