package com.honkai.blog.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.honkai.blog.bean.Result;
import com.honkai.blog.bean.Verify;
import com.honkai.blog.bean.dto.auth.LoginDto;
import com.honkai.blog.bean.dto.auth.RegisterDto;
import com.honkai.blog.bean.vo.auth.LoginVo;
import com.honkai.blog.bean.vo.auth.RegisterVo;
import com.honkai.blog.db.entity.User;
import com.honkai.blog.db.mapper.UserMapper;
import com.honkai.blog.db.service.UserService;
import com.honkai.blog.utils.AuthUtil;
import com.honkai.blog.utils.RedisUtil;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/auth")
@Tag(name = "权限管理")
public class AuthController {

    @Resource
    UserService userService;

    @Resource
    RedisUtil redisUtil;

    @GetMapping("/verify")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String key = request.getParameter("key");
        if (redisUtil.containsKey(key)) {
            response.sendError(400);
            return;
        }
        ShearCaptcha shearCaptcha = CaptchaUtil.createShearCaptcha(150, 40, 5, 4);
        response.setContentType("image/jpeg");
        response.setHeader("Pragma", "No-cache");
        shearCaptcha.write(response.getOutputStream());
        Verify verify = new Verify();
        verify.setKey(key);
        verify.setValue(shearCaptcha.getCode());
        redisUtil.set(key, verify, 60000L);
    }

    @Operation(summary = "注册")
    @PostMapping("/register")
    public Result<Object> register(@RequestBody RegisterVo registerVo) {
        if (!redisUtil.containsKey(registerVo.getKey())) {
            return Result.fail("验证码已过期");
        }
        Verify verify = redisUtil.get(registerVo.getKey(), Verify.class);
        if (!verify.getValue().toLowerCase().equals(registerVo.getVerify().toLowerCase())) {
            return Result.fail("验证码错误");
        }
        redisUtil.remove(registerVo.getKey());
        if (userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getPhone, registerVo.getPhone())) != null) {
            return Result.fail("该手机号已被注册");
        }
        User user = new User();
        BeanUtils.copyProperties(registerVo, user);
        user.setRole(0);
        userService.save(user);
        RegisterDto registerDto = new RegisterDto();
        registerDto.setToken(authUtil.generateToken(user.getId()));
        registerDto.setUserId(user.getId());
        registerDto.setRole(user.getRole());
        registerDto.setUsername(user.getName());
        return Result.success(registerDto);
    }

    @Resource
    AuthUtil authUtil;

    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<LoginDto> login(@RequestBody LoginVo loginVo) {
        if (!redisUtil.containsKey(loginVo.getKey())) {
            return Result.fail("验证码已过期");
        }
        Verify verify = redisUtil.get(loginVo.getKey(), Verify.class);
        if (!verify.getValue().equals(loginVo.getVerify())) {
            return Result.fail("验证码错误");
        }
        User user = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getPhone, loginVo.getPhone()).eq(User::getPassword, loginVo.getPassword()));
        if (user == null) {
            return Result.fail("手机号或密码错误");
        }
        LoginDto loginDto = new LoginDto();
        loginDto.setToken(authUtil.generateToken(
                user.getId()));
        loginDto.setRole(user.getRole());
        loginDto.setUserId(user.getId());
        loginDto.setUsername(user.getName());
        return Result.success(loginDto);
    }

    @Resource
    UserMapper userMapper;

    @Operation(summary = "登录 - SQL注入")
    @PostMapping("/login/sql")
    public Result<LoginDto> loginSql(@RequestBody LoginVo loginVo) {
        if (!redisUtil.containsKey(loginVo.getKey())) {
            return Result.fail("验证码已过期");
        }
        Verify verify = redisUtil.get(loginVo.getKey(), Verify.class);
        if (!verify.getValue().equals(loginVo.getVerify())) {
            return Result.fail("验证码错误");
        }
        List<User> users = userMapper.getUserSql(loginVo.getPhone(), loginVo.getPassword());
        log.info("size of users: " + users.size());
        User user = users.get(0);
        if (user == null) {
            return Result.fail("手机号或密码错误");
        }
        LoginDto loginDto = new LoginDto();
        loginDto.setToken(authUtil.generateToken(
                user.getId()));
        loginDto.setUsername(user.getName());
        loginDto.setUserId(user.getId());
        loginDto.setRole(user.getRole());
        return Result.success(loginDto);
    }
}
