package com.cuit.interviewsystem.model.dto.user;


import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UserLoginDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -4603025172805125527L;
    /**
     * account 可能为用户名username，手机号phone，邮箱email
     */
    private String account;
    private String password;
}
