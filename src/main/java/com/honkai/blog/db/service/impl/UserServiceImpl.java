package com.honkai.blog.db.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.honkai.blog.db.entity.User;
import com.honkai.blog.db.mapper.UserMapper;
import com.honkai.blog.db.service.UserService;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
