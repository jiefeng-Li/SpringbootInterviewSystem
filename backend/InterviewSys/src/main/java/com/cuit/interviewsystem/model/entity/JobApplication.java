package com.cuit.interviewsystem.model.entity;

import com.baomidou.mybatisplus.annotation.*;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 简历投递记录表
 * @TableName t_job_application
 */
@TableName(value ="t_job_application")
@Data
public class JobApplication {
    /**
     * 投递记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 投递的公司
     */
    @NotNull(message = "公司不能为空")
    private Long companyId;

    /**
     * 职位ID
     */
    @NotNull(message = "职位不能为空")
    private Long jobPositionId;

    /**
     * 求职者用户ID
     */
    @NotNull(message = "用户不能为空")
    private Long userId;

    /**
     * 投递时使用的简历ID
     */
    @NotNull(message = "简历不能为空")
    private Long resumeId;

    /**
     * 投递时间
     */
    private LocalDate applyTime;

    /**
     * 状态(0待处理,1已查看,2初筛通过,3初筛不通过,4面试中,5已发Offer,6已录用,7已淘汰)
     */
    private Integer status;

    /**
     * 求职信
     */
    private String coverLetter;

    /**
     * HR备注
     */
    private String remarks;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private LocalDate createTime;

    /**
     * 更新时间
     */
    private LocalDate updateTime;
}