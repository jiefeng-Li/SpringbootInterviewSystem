package com.cuit.interviewsystem.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import lombok.Data;

/**
 * 公司基本信息表
 * @TableName t_company
 */
@TableName(value ="t_company")
@Data
public class Company {
    /**
     * 公司ID
     */
    @TableId(type = IdType.AUTO)
    private Long company_id;

    /**
     * 企业管理员ID 对应用户表user_id
     */
    private Long admin_id;

    /**
     * 公司全称
     */
    private String company_name;

    /**
     * Logo URL
     */
    private String logo_url;

    /**
     * 官网
     */
    private String website;

    /**
     * 公司简介
     */
    private String introduction;

    /**
     * 所属行业
     */
    private String industry;

    /**
     * 公司规模
     */
    private String scale;

    /**
     * 所在城市
     */
    private String city;

    /**
     * 状态(0待审,1正常,2驳回,3禁用)
     */
    private Integer status;

    /**
     * 营业执照URL
     */
    private String business_license_url;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer is_deleted;

    /**
     * 创建时间
     */
    private Date create_time;

    /**
     * 更新时间
     */
    private Date update_time;

    /**
     * 系统更新时间
     */
    private Date edit_time;
}