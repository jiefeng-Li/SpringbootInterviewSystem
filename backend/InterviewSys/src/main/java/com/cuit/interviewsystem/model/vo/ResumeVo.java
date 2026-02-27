package com.cuit.interviewsystem.model.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ResumeVo {
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 简历模板ID
     */
    private Integer templateId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 性别(0男,1女,2保密)
     */
    private Integer gender;

    /**
     * 出生日期
     */
    private Date birthday;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 现居地址
     */
    private String address;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 期望工作城市
     */
    private String city;

    /**
     * 个人简介/求职意向
     */
    private String summary;

    /**
     * 是否默认简历（用于多份简历管理）
     */
    private Integer isDefault;

    /**
     *
     */
    private Date createTime;

    private List<ResumeEducationVo> educations;
    private List<ResumeExperienceVo> experiences;
    private List<ResumeProjectVo> projects;
}
