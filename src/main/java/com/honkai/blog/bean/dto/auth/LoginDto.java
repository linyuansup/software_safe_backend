package com.honkai.blog.bean.dto.auth;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class LoginDto {
    @Schema(description = "token")
    private String token;

    @Schema(description = "角色")
    private Integer role;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "id")
    private Integer userId;
}
