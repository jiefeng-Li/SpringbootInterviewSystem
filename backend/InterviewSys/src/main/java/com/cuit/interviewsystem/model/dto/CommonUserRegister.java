package com.cuit.interviewsystem.model.dto;

import lombok.Data;

import java.util.Date;

@Data
public class CommonUserRegister {
    /**
     * 用户名
     */
    private String username;

    /**
     * 用户角色(JOB_SEEKER //求职者, RECRUITER//招聘者, COMP_ADMIN//企业管理员, SYS_ADMIN//系统管理员)
     */
    private String role;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 密码
     */
    private String password;
}
