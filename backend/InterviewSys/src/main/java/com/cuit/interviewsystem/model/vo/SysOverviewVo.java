package com.cuit.interviewsystem.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysOverviewVo {
    private Long todayActiveUserCount;
    private Long weekActiveUserCount;
    private Long weekPublishedJobCount;
    private Long totalCompanyCount;
    private List<CompanyStatusStatVo> companyStatusStats;
    private Long todayJobApplicationCount;
    private LocalDate weekStart;
    private LocalDate today;
}
