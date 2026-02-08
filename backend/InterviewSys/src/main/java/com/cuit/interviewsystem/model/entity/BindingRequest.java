package com.cuit.interviewsystem.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import lombok.Data;

/**
 * 招聘者绑定公司申请记录表
 * @TableName t_binding_request
 */
@TableName(value ="t_binding_request")
@Data
public class BindingRequest {
    /**
     * 申请记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 申请人ID（招聘者）
     */
    private Long userId;

    /**
     * 目标公司ID
     */
    private Long companyId;

    /**
     * 申请备注
     */
    private String applicationNotes;

    /**
     * 状态(0待审,1通过,2拒绝,3取消)
     */
    private Integer status;

    /**
     * 审核人ID（公司管理员）
     */
    private Long reviewedBy;

    /**
     * 审核意见
     */
    private String reviewNotes;

    /**
     * 审核时间
     */
    private Date reviewedTime;

    /**
     * 申请失效时间
     */
    private Date expiresAt;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}