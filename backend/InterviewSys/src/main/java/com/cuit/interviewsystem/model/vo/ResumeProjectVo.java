package com.cuit.interviewsystem.model.vo;

import lombok.Data;

import java.util.Date;

@Data
public class ResumeProjectVo {
    private Long id;

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
