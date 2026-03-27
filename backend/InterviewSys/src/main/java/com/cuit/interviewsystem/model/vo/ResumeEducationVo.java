package com.cuit.interviewsystem.model.vo;

import lombok.Data;

import java.time.LocalDate;


@Data
public class ResumeEducationVo {
    private Long id;

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
    private LocalDate startDate;

    /**
     * 毕业时间（或至今）
     */
    private LocalDate endDate;

    /**
     * 在校经历、荣誉等
     */
    private String description;
}
