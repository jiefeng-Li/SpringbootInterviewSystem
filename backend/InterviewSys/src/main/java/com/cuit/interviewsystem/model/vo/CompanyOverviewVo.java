package com.cuit.interviewsystem.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyOverviewVo {
    private Long todayPublishedJobCount;
    private Long weekPublishedJobCount;
    private Long todayApplicationCount;
    private Long weekApplicationCount;
    private Long todayOfferCount;
    private Long weekOfferCount;
    private Long companyUserCount;
    private LocalDate weekStart;
    private LocalDate today;
}
