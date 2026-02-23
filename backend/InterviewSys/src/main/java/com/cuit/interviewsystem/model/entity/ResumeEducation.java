package com.cuit.interviewsystem.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 简历教育经历
 * @TableName t_resume_education
 */
@TableName(value ="t_resume_education")
@Data
public class ResumeEducation {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 简历ID
     */
    @NotBlank(message = "教育经历简历ID不能为空")
    private Long resumeId;

    /**
     * 学校名称
     */
    private String school;

    /**
     * 专业
     */
    private String major;

    /**
     * 学历
     */
    private String degree;

    /**
     * 开始时间
     */
    private Date startDate;

    /**
     * 毕业时间（或至今）
     */
    private Date endDate;

    /**
     * 在校经历、荣誉等
     */
    private String description;
}