package com.cuit.interviewsystem.controller;


import com.cuit.interviewsystem.annotation.AuthCheck;
import com.cuit.interviewsystem.common.Result;
import com.cuit.interviewsystem.model.dto.jobApplication.JobApplicationPageDto;
import com.cuit.interviewsystem.model.enums.UserRoleEnum;
import com.cuit.interviewsystem.service.CompanyService;
import com.cuit.interviewsystem.service.JobApplicationService;
import com.cuit.interviewsystem.service.JobPositionService;
import com.cuit.interviewsystem.service.UserService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
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
    public Result<?> getJobApplicationList(@Valid JobApplicationPageDto jobApplicationPageDto) {
        jobApplicationService.getJobApplicationList(jobApplicationPageDto);
        return Result.success();
    }
}
