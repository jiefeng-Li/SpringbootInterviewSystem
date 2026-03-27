package com.cuit.interviewsystem.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

/**
 * Offer记录表
 * @TableName t_offer_record
 */
@TableName(value ="t_offer_record")
@Data
public class OfferRecord {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 投递记录ID
     */
    private Long jobApplicationId;

    /**
     * 公司ID
     */
    private Long companyId;

    /**
     * 求职者用户ID
     */
    private Long jobSeekerId;

    /**
     * 发送Offer的招聘者ID
     */
    private Long recruiterId;

    /**
     * 关联面试通知ID
     */
    private Long interviewNoticeId;

    /**
     * 职位ID
     */
    private Long jobPositionId;

    /**
     * 预计入职日期
     */
    private LocalDate expectedEntryDate;

    /**
     * 税前月薪
     */
    private BigDecimal salaryMonthly;

    /**
     * 年薪月数（如13薪）
     */
    private Integer salaryMonthCount;

    /**
     * 试用期（月）
     */
    private Integer probationMonths;

    /**
     * Offer确认截止时间
     */
    private LocalDate offerDeadline;

    /**
     * 状态(0待确认,1已接受,2已拒绝,3已过期,4已撤回,5已取消)
     */
    private Integer status;

    /**
     * 福利说明
     */
    private String welfare;

    /**
     * 备注
     */
    private String remark;

    /**
     * 拒绝原因
     */
    private String rejectReason;

    /**
     * 候选人响应时间
     */
    private LocalDate respondTime;

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