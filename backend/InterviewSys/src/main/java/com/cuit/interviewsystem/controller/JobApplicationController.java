package com.cuit.interviewsystem.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cuit.interviewsystem.annotation.AuthCheck;
import com.cuit.interviewsystem.common.Result;
import com.cuit.interviewsystem.model.dto.UpdateJobApplicationDto;
import com.cuit.interviewsystem.model.dto.jobApplication.AddJobApplicationDto;
import com.cuit.interviewsystem.model.dto.jobApplication.JobApplicationPageDto;
import com.cuit.interviewsystem.model.enums.UserRoleEnum;
import com.cuit.interviewsystem.model.vo.JobApplicationVo;
import com.cuit.interviewsystem.model.vo.PageVo;
import com.cuit.interviewsystem.service.CompanyService;
import com.cuit.interviewsystem.service.JobApplicationService;
import com.cuit.interviewsystem.service.JobPositionService;
import com.cuit.interviewsystem.service.UserService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/jobApplication")
public class JobApplicationController {
    @Resource
    private JobApplicationService jobApplicationService;
    @Resource
    private UserService userService;
    @Resource
    private JobPositionService jobPositionService;
    @Resource
    private CompanyService companyService;


    @GetMapping("/list")
    @AuthCheck(roles = {UserRoleEnum.RECRUITER})
    public Result<PageVo<JobApplicationVo>> getJobApplicationList(@Valid JobApplicationPageDto jobApplicationPageDto) {
        Page<JobApplicationVo> jobApplicationList = jobApplicationService.getJobApplicationList(jobApplicationPageDto);
        return Result.success(PageVo.of(jobApplicationList));
    }

    @PostMapping("")
    @AuthCheck(roles = {UserRoleEnum.RECRUITER})
    public Result<?> addJobApplication(@Valid AddJobApplicationDto dto) {
         jobApplicationService.addJobApplication(dto);
         return Result.success("投递成功");
    }


    @GetMapping
    @AuthCheck(roles = {UserRoleEnum.RECRUITER, UserRoleEnum.JOB_SEEKER})
    public Result<JobApplicationVo> getJobApplicationById(Long id) {
        JobApplicationVo res = jobApplicationService.getJobApplicationById(id);
        return Result.success(res);
    }

    @PostMapping("/review")
    @AuthCheck(roles = {UserRoleEnum.RECRUITER})
    public Result<?> reviewJobApplication(@Valid UpdateJobApplicationDto dto) {
        jobApplicationService.updateJobApplicationStatus(dto);
        return Result.success("操作成功");
    }
}
