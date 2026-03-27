package com.cuit.interviewsystem.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cuit.interviewsystem.annotation.AuthCheck;
import com.cuit.interviewsystem.common.Result;
import com.cuit.interviewsystem.model.dto.jobApplication.OwnJobApplicationPageDto;
import com.cuit.interviewsystem.model.dto.jobApplication.UpdateJobApplicationBatchDto;
import com.cuit.interviewsystem.model.dto.jobApplication.UpdateJobApplicationDto;
import com.cuit.interviewsystem.model.dto.jobApplication.AddJobApplicationDto;
import com.cuit.interviewsystem.model.dto.jobApplication.JobApplicationPageDto;
import com.cuit.interviewsystem.model.enums.UserRoleEnum;
import com.cuit.interviewsystem.model.vo.JobApplicationVo;
import com.cuit.interviewsystem.model.vo.PageVo;
import com.cuit.interviewsystem.service.JobApplicationService;
import com.cuit.interviewsystem.service.ResumeTemplateService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/jobApplication")
@Tag(name = "职位申请相关接口")
public class JobApplicationController {
    @Resource
    private JobApplicationService jobApplicationService;
    @Resource
    private ResumeTemplateService templateService;

    @GetMapping("/list")
    @AuthCheck(roles = {UserRoleEnum.RECRUITER})
    public Result<PageVo<JobApplicationVo>> getJobApplicationList(@Valid JobApplicationPageDto jobApplicationPageDto) {
        Page<JobApplicationVo> jobApplicationList = jobApplicationService.getJobApplicationList(jobApplicationPageDto);
        return Result.success(PageVo.of(jobApplicationList));
    }

    @GetMapping("/list/own")
    @AuthCheck(roles = {UserRoleEnum.JOB_SEEKER})
    public Result<PageVo<JobApplicationVo>> getJobApplicationListByJobSeeker(@Valid OwnJobApplicationPageDto pageDto) {
        Page<JobApplicationVo> page = jobApplicationService.getOwnApplicationList(pageDto);
        return Result.success(PageVo.of(page));
    }

    @PostMapping("")
    @AuthCheck(roles = {UserRoleEnum.JOB_SEEKER})
    public Result<?> addJobApplication(@Valid @RequestBody AddJobApplicationDto dto) {
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
    public Result<?> reviewJobApplication(@Valid @RequestBody UpdateJobApplicationDto dto) {
        jobApplicationService.updateJobApplicationStatus(dto);
        return Result.success("操作成功");
    }

    @PostMapping("/review/batch")
    @AuthCheck(roles = {UserRoleEnum.RECRUITER})
    public Result<?> reviewJobApplicationBatch(@Valid @RequestBody UpdateJobApplicationBatchDto dto) {
        int successCount = jobApplicationService.updateJobApplicationStatusBatch(dto);
        return Result.success(successCount, "批量操作成功");
    }
}
