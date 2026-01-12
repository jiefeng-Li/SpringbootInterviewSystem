package com.cuit.interviewsystem.model.vo;


import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginVo implements Serializable {

    private Long userId;

    private String role;

    private Long companyId;
}
