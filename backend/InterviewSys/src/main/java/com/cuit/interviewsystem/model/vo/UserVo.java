package com.cuit.interviewsystem.model.vo;

import lombok.Data;

import java.util.Date;


@Data
public class UserVo {
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户角色(JOB_SEEKER //求职者, RECRUITER//招聘者, COMP_ADMIN//企业管理员, SYS_ADMIN//系统管理员)
     */
    private String role;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 个人简介
     */
    private String profile;

    /**
     * 账户状态(0禁用,1正常,2锁定)
     */
    private String accountStatus;

    /**
     * 最后登录时间
     */
    private Date lastLoginTime;

    /**
     * 逻辑删除(0未删,1已删)
     */
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 系统更新时间
     */
    private Date editTime;

    /**
     * 用户所属公司ID
     */
    private Long companyId;

    private String companyName;
}
