package com.honkai.blog.db.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.honkai.blog.db.entity.Blog;

public interface BlogService extends IService<Blog> {
    public List<Blog> getAllDynamic();
}