package com.honkai.blog.bean.vo.dynamic;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "删除博客")
public class DeleteBlogVo {
    @Schema(description = "博客id")
    private Integer id;
}
