package com.honkai.blog.db.service.impl;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.honkai.blog.db.entity.Blog;
import com.honkai.blog.db.mapper.BlogMapper;
import com.honkai.blog.db.service.BlogService;

@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService {

    @Override
    public List<Blog> getAllDynamic() {
        List<Blog> result = list(new LambdaQueryWrapper<Blog>());
        result.sort(new Comparator<Blog>() {
            @Override
            public int compare(Blog arg0, Blog arg1) {
                return -arg0.getUpdateTime().compareTo(arg1.getUpdateTime());
            }
        });
        return result;
    }

}
