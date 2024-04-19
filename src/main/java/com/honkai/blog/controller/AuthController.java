package com.honkai.blog.controller;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.honkai.blog.bean.Result;
import com.honkai.blog.bean.dto.auth.LoginDto;
import com.honkai.blog.bean.dto.auth.RegisterDto;
import com.honkai.blog.bean.vo.auth.LoginVo;
import com.honkai.blog.bean.vo.auth.RegisterVo;
import com.honkai.blog.db.entity.User;
import com.honkai.blog.db.mapper.UserMapper;
import com.honkai.blog.db.service.UserService;
import com.honkai.blog.utils.AuthUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/auth")
@Tag(name = "权限管理")
public class AuthController {

    @Resource
    UserService userService;

    @Operation(summary = "注册")
    @PostMapping("/register")
    public Result<Object> register(@RequestBody RegisterVo registerVo) {
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
