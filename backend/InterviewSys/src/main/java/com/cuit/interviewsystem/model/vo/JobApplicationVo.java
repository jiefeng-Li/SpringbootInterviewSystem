package com.cuit.interviewsystem.model.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class JobApplicationVo {
    /**
     * 投递记录ID
     */
    private Long id;

    /**
     * 投递的公司
     */
    @NotNull
    private Long companyId;

    /**
     * 职位ID
     */
    @NotNull
    private Long jobPositionId;

    private String jobTitle;
    /**
     * 求职者用户ID
     */
    @NotNull
    private Long userId;
    private String jobSeekerName;

    /**
     * 投递时使用的简历ID
     */
    private Long resumeId;
    private ResumeVo resume;

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
}
