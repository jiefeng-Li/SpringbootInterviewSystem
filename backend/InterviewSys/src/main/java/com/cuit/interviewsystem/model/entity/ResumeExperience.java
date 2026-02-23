package com.cuit.interviewsystem.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 简历工作经历
 * @TableName t_resume_experience
 */
@TableName(value ="t_resume_experience")
@Data
public class ResumeExperience {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 简历ID
     */
    @NotBlank(message = "工作经历简历ID不能为空")
    private Long resumeId;

    /**
     * 公司名称
     */
    private String company;

    /**
     * 职位名称
     */
    private String position;

    /**
     * 开始时间
     */
    private Date startDate;

    /**
     * 结束时间（或至今）
     */
    private Date endDate;

    /**
     * 工作内容、业绩等
     */
    private String description;
}