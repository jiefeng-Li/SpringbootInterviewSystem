package com.cuit.interviewsystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cuit.interviewsystem.common.Result;
import com.cuit.interviewsystem.model.dto.job.JobSearchPageDto;
import com.cuit.interviewsystem.model.entity.JobPosition;
import com.cuit.interviewsystem.model.vo.JobPositionVo;
import com.cuit.interviewsystem.model.vo.PageVo;
import com.cuit.interviewsystem.service.CompanyService;
import com.cuit.interviewsystem.service.JobPositionService;
import com.cuit.interviewsystem.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/job")
public class JobPositionController {
    @Resource
    private JobPositionService jobPositionService;
    @Resource
    private CompanyService companyService;
    @Resource
    private UserService userService;

    @GetMapping("")
    public Result<JobPositionVo> getJobPositionById(Long id){
        JobPosition jobPosition = jobPositionService.getJobPositionById(id);
        JobPositionVo res = JobPositionVo.objToVo(jobPosition);
        if (res != null) {
            res.setCompanyName(companyService.getCompanyById(jobPosition.getCompanyId()).getCompanyName());
            res.setHiringManagerName(userService.getById(jobPosition.getHiringManagerId()).getUsername());
        }
        return Result.success(res);
    }

    @GetMapping("/list")
    public Result<PageVo<JobPositionVo>> getJobPositionList(JobSearchPageDto jobSearchPageDto){
        Page<JobPositionVo> res = jobPositionService.getJobPositionList(jobSearchPageDto);
    }
}
