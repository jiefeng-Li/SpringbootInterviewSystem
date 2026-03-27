package com.cuit.interviewsystem.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class OfferRecordVo {
    private Long id;
    private Long jobApplicationId;
    private Long companyId;
    private Long jobSeekerId;
    private Long recruiterId;
    private Long interviewNoticeId;
    private Long jobPositionId;

    private LocalDate expectedEntryDate;
    private BigDecimal salaryMonthly;
    private Integer salaryMonthCount;
    private Integer probationMonths;
    private LocalDate offerDeadline;

    private Integer status;
    private String welfare;
    private String remark;
    private String rejectReason;
    private LocalDate respondTime;

    private LocalDate createTime;
    private LocalDate updateTime;

    private String companyName;
    private String jobSeekerName;
    private String recruiterName;
    private String jobTitle;
}
