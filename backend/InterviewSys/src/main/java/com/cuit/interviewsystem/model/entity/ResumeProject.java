package com.cuit.interviewsystem.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 简历项目经历
 * @TableName t_resume_project
 */
@TableName(value ="t_resume_project")
@Data
public class ResumeProject {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 简历ID
     */
    @NotBlank(message = "项目经历简历ID不能为空")
    private Long resumeId;

    /**
     * 项目名称
     */
    private String name;

    /**
     * 项目描述
     */
    private String description;

    /**
     * 开始时间
     */
    private Date startDate;

    /**
     * 结束时间（或至今）
     */
    private Date endDate;
}