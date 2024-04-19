package com.honkai.blog.bean.vo.dynamic;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "推送博客")
public class PushBlogVo {
    @Schema(description = "博客内容")
    private String content;
}
