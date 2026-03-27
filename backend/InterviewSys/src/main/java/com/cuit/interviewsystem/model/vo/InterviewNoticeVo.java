package com.cuit.interviewsystem.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewNoticeVo {
    private Long id;

    private Long jobApplicationId;

    private Long intervieweeId;

    private Long companyId;

    private String companyName;

    private LocalDate interviewTime;

    private String interviewAddress;
    private String comment;
}
