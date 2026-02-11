package com.cuit.interviewsystem.model.dto.user;

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
    private String username;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 密码
     */
    private String password;
}
