package com.cuit.interviewsystem.model.vo;

import lombok.Data;

import java.util.Date;

@Data
public class ResumeExperienceVo {
    private Long id;
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
