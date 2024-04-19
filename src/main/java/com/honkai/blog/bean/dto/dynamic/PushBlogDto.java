package com.honkai.blog.bean.dto.dynamic;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PushBlogDto {
    @Schema(description = "动态id")
    private Integer id;
}
