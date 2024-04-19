
package com.honkai.blog.bean;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "分页记录")
public class PageResult<T> {

    @Schema(description = "总数")
    private Long total;

    @Schema(description = "记录")
    private List<T> records;

}
