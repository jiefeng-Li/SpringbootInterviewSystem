package com.cuit.interviewsystem.model.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


@Data
public class UserRegisterDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 4726859841415482675L;
    /**
     * 用户名
     */
    @NotNull(message = "用户名不能为空")
    private String username;
    /**
     * 手机号
     */
    @NotNull(message = "手机号不能为空")
    private String phone;
    /**
     * 密码
     */
    @NotNull(message = "密码不能为空")
    private String password;
}
