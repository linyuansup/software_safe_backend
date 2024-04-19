package com.honkai.blog.bean.vo.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "登录")
public class LoginVo {

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "密码")
    private String password;
}
