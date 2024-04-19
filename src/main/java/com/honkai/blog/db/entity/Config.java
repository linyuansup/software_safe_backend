package com.honkai.blog.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "配置实体")
public class Config {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("`key`")
    @Schema(description = "键")
    private String key;

    @TableField("`value`")
    @Schema(description = "值")
    private String value;

    @Schema(description = "备注")
    private String remark;

}
