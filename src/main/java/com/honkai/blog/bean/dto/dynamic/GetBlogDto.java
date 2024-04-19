package com.honkai.blog.bean.dto.dynamic;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GetBlogDto {

    @Schema(description = "博客")
    private List<Blog> blogs;

    @Data
    public class Blog {
        @Schema(description = "发送方")
        private String sender;

        @Schema(description = "内容")
        private String content;

        @Schema(description = "时间")
        private String time;

        @Schema(description = "id")
        private Integer id;

        @Schema(description = "发送方id")
        private Integer senderId;
    }
}
