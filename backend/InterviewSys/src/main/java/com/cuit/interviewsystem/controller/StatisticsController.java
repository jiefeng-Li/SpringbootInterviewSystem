package com.cuit.interviewsystem.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cuit.interviewsystem.annotation.AuthCheck;
import com.cuit.interviewsystem.common.Result;
import com.cuit.interviewsystem.exception.ErrorEnum;
import com.cuit.interviewsystem.mapper.CompanyMapper;
import com.cuit.interviewsystem.mapper.JobApplicationMapper;
import com.cuit.interviewsystem.mapper.JobPositionMapper;
import com.cuit.interviewsystem.mapper.OfferRecordMapper;
import com.cuit.interviewsystem.mapper.UserMapper;
import com.cuit.interviewsystem.model.entity.Company;
import com.cuit.interviewsystem.model.entity.JobApplication;
import com.cuit.interviewsystem.model.entity.JobPosition;
import com.cuit.interviewsystem.model.entity.OfferRecord;
import com.cuit.interviewsystem.model.entity.User;
import com.cuit.interviewsystem.model.enums.CompanyStatusEnum;
import com.cuit.interviewsystem.model.enums.UserRoleEnum;
import com.cuit.interviewsystem.model.vo.CompanyOverviewVo;
import com.cuit.interviewsystem.model.vo.CompanyStatusStatVo;
import com.cuit.interviewsystem.model.vo.SysOverviewVo;
import com.cuit.interviewsystem.utils.JWTUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/statistics")
@Tag(name = "统计面板")
public class StatisticsController {

    @Resource
    private UserMapper userMapper;
    @Resource
    private JobPositionMapper jobPositionMapper;
    @Resource
    private CompanyMapper companyMapper;
    @Resource
    private JobApplicationMapper jobApplicationMapper;
    @Resource
    private OfferRecordMapper offerRecordMapper;
    @Resource
    private JWTUtil jwtUtil;

    @GetMapping("/sys/overview")
    @AuthCheck(roles = {UserRoleEnum.SYS_ADMIN})
    public Result<SysOverviewVo> getSysOverview() {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = getWeekStart(today);

        long todayActiveUserCount = safeCount(userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getIsDeleted, 0)
                .eq(User::getLastLoginTime, today)));

        long weekActiveUserCount = safeCount(userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getIsDeleted, 0)
                .between(User::getLastLoginTime, weekStart, today)));

        long weekPublishedJobCount = safeCount(jobPositionMapper.selectCount(new LambdaQueryWrapper<JobPosition>()
                .eq(JobPosition::getIsDeleted, 0)
                .between(JobPosition::getCreateTime, weekStart, today)));

        long todayJobApplicationCount = safeCount(jobApplicationMapper.selectCount(new LambdaQueryWrapper<JobApplication>()
                .eq(JobApplication::getIsDeleted, 0)
                .eq(JobApplication::getApplyTime, today)));

        long totalCompanyCount = safeCount(companyMapper.selectCount(new LambdaQueryWrapper<Company>()
                .eq(Company::getIsDeleted, 0)));

        List<CompanyStatusStatVo> companyStatusStats = new ArrayList<>();
        for (CompanyStatusEnum statusEnum : CompanyStatusEnum.values()) {
            long count = safeCount(companyMapper.selectCount(new LambdaQueryWrapper<Company>()
                    .eq(Company::getIsDeleted, 0)
                    .eq(Company::getStatus, statusEnum.getStatus())));
            companyStatusStats.add(new CompanyStatusStatVo(statusEnum.getStatus(), statusEnum.getText(), count));
        }

        SysOverviewVo res = new SysOverviewVo(
                todayActiveUserCount,
                weekActiveUserCount,
                weekPublishedJobCount,
                totalCompanyCount,
                companyStatusStats,
                todayJobApplicationCount,
                weekStart,
                today
        );
        return Result.success(res);
    }

    @GetMapping("/company/overview")
    @AuthCheck(roles = {UserRoleEnum.COMP_ADMIN})
    public Result<CompanyOverviewVo> getCompanyOverview() {
        User currentUser = jwtUtil.parseLoginUser();
        if (currentUser == null || currentUser.getCompanyId() == null) {
            return Result.error(ErrorEnum.PARAMS_ERROR, "当前用户未绑定企业");
        }

        Long companyId = currentUser.getCompanyId();
        LocalDate today = LocalDate.now();
        LocalDate weekStart = getWeekStart(today);

        long todayPublishedJobCount = safeCount(jobPositionMapper.selectCount(new LambdaQueryWrapper<JobPosition>()
                .eq(JobPosition::getIsDeleted, 0)
                .eq(JobPosition::getCompanyId, companyId)
                .eq(JobPosition::getCreateTime, today)));

        long weekPublishedJobCount = safeCount(jobPositionMapper.selectCount(new LambdaQueryWrapper<JobPosition>()
                .eq(JobPosition::getIsDeleted, 0)
                .eq(JobPosition::getCompanyId, companyId)
                .between(JobPosition::getCreateTime, weekStart, today)));

        long todayApplicationCount = safeCount(jobApplicationMapper.selectCount(new LambdaQueryWrapper<JobApplication>()
                .eq(JobApplication::getIsDeleted, 0)
                .eq(JobApplication::getCompanyId, companyId)
                .eq(JobApplication::getApplyTime, today)));

        long weekApplicationCount = safeCount(jobApplicationMapper.selectCount(new LambdaQueryWrapper<JobApplication>()
                .eq(JobApplication::getIsDeleted, 0)
                .eq(JobApplication::getCompanyId, companyId)
                .between(JobApplication::getApplyTime, weekStart, today)));

        long todayOfferCount = safeCount(offerRecordMapper.selectCount(new LambdaQueryWrapper<OfferRecord>()
                .eq(OfferRecord::getIsDeleted, 0)
                .eq(OfferRecord::getCompanyId, companyId)
                .eq(OfferRecord::getCreateTime, today)));

        long weekOfferCount = safeCount(offerRecordMapper.selectCount(new LambdaQueryWrapper<OfferRecord>()
                .eq(OfferRecord::getIsDeleted, 0)
                .eq(OfferRecord::getCompanyId, companyId)
                .between(OfferRecord::getCreateTime, weekStart, today)));

        long companyUserCount = safeCount(userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getIsDeleted, 0)
                .eq(User::getCompanyId, companyId)));

        CompanyOverviewVo res = new CompanyOverviewVo(
                todayPublishedJobCount,
                weekPublishedJobCount,
                todayApplicationCount,
                weekApplicationCount,
                todayOfferCount,
                weekOfferCount,
                companyUserCount,
                weekStart,
                today
        );
        return Result.success(res);
    }

    private LocalDate getWeekStart(LocalDate date) {
        return date.with(DayOfWeek.MONDAY);
    }

    private long safeCount(Long count) {
        return count == null ? 0L : count;
    }
}
