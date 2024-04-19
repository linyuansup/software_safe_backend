package com.honkai.blog.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.honkai.blog.annotation.RequireAuth;
import com.honkai.blog.bean.Result;
import com.honkai.blog.bean.dto.dynamic.DeleteBlogDto;
import com.honkai.blog.bean.dto.dynamic.GetBlogDto;
import com.honkai.blog.bean.dto.dynamic.PushBlogDto;
import com.honkai.blog.bean.vo.dynamic.DeleteBlogVo;
import com.honkai.blog.bean.vo.dynamic.PushBlogVo;
import com.honkai.blog.db.entity.Blog;
import com.honkai.blog.db.entity.User;
import com.honkai.blog.db.service.BlogService;
import com.honkai.blog.db.service.UserService;
import com.honkai.blog.utils.AuthUtil;
import com.honkai.blog.utils.DateFormatUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;

@RestController
@RequireAuth
@RequestMapping("/blog")
@Tag(name = "博客管理")
public class BlogController {

    @Resource
    BlogService blogService;

    @Resource
    UserService userService;

    @Resource
    AuthUtil authUtil;

    @Operation(summary = "发布博客")
    @PostMapping("/publish")
    public Result<PushBlogDto> publish(@RequestBody PushBlogVo vo) {
        Blog dynamic = new Blog();
        dynamic.setContent(vo.getContent());
        dynamic.setSender(authUtil.getUserId());
        blogService.save(dynamic);
        PushBlogDto dto = new PushBlogDto();
        dto.setId(dynamic.getId());
        return Result.success(dto);
    }

    @Operation(summary = "获取博客")
    @PostMapping("/get")
    public Result<GetBlogDto> get() {
        List<Blog> blog = blogService.getAllDynamic();
        GetBlogDto result = new GetBlogDto();
        result.setBlogs(new ArrayList<>());
        for (Blog dynamic : blog) {
            GetBlogDto.Blog dto = result.new Blog();
            dto.setContent(dynamic.getContent());
            dto.setId(dynamic.getId());
            dto.setSender(userService.getById(dynamic.getSender()).getName());
            dto.setTime(DateFormatUtil.format(dynamic.getCreateTime()));
            dto.setSenderId(dynamic.getSender());
            result.getBlogs().add(dto);
        }
        return Result.success(result);
    }

    @Operation(summary = "删除动态")
    @PostMapping("/delete")
    public Result<DeleteBlogDto> delete(@RequestBody DeleteBlogVo vo) {
        User user = userService.getById(authUtil.getUserId());
        Blog dynamic = blogService.getById(vo.getId());
        if (user.getRole() != 1 && dynamic.getSender() != authUtil.getUserId()) {
            return Result.fail("权限不足");
        }
        blogService.removeById(vo.getId());
        return Result.success(new DeleteBlogDto());
    }
}
