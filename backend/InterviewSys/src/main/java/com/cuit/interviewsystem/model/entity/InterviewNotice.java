package com.cuit.interviewsystem.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 面试通知表
 * @TableName t_interview_notice
 */
@TableName(value ="t_interview_notice")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewNotice {
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
     * 面试者ID
     */
    private Long intervieweeId;

    /**
     * 公司ID
    */
    private Long companyId;

    /**
     * 面试时间
     */
    private LocalDate interviewTime;

    /**
     * 面试地址
     */
    private String interviewAddress;

    /**
     * 备注
     */
    private String comment;

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