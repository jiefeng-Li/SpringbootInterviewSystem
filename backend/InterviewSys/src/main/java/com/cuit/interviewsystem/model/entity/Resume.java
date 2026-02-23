package com.cuit.interviewsystem.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 简历基本信息
 * @TableName t_resume
 */
@TableName(value ="t_resume")
@Data
public class Resume {
    /**
     * 简历ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @NotBlank(message = "简历用户ID不能为空")
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
     * 逻辑删除
     */
    @TableLogic
    private Integer isDeleted;
    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;
}