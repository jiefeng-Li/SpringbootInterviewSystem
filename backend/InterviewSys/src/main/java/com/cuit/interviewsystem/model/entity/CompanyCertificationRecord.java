package com.cuit.interviewsystem.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import lombok.Data;

/**
 * 公司资质认证记录表
 * @TableName t_company_certification_record
 */
@TableName(value ="t_company_certification_record")
@Data
public class CompanyCertificationRecord {
    /**
     * 资质记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 公司ID
     */
    private Long companyId;

    /**
     * 企业管理员ID 对应用户表user_id
     */
    private Long adminId;

    /**
     * 联系人
     */
    private String contactName;

    /**
     * 联系人电话
     */
    private String contactPhone;

    /**
     * 联系人邮箱
     */
    private String contactEmail;

    /**
     * 联系人职位
     */
    private String contactPosition;

    /**
     * 状态(0待审,1通过,2驳回)
     */
    private Integer status;

    /**
     * 审核意见
     */
    private String reviewNotes;

    /**
     * 审核人ID
     */
    private Long reviewedBy;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer isDeleted;

    /**
     * 审核时间
     */
    private Date reviewedTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}