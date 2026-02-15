package com.cuit.interviewsystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cuit.interviewsystem.annotation.AuthCheck;
import com.cuit.interviewsystem.common.Result;
import com.cuit.interviewsystem.exception.ErrorEnum;
import com.cuit.interviewsystem.model.dto.UpdateJobDto;
import com.cuit.interviewsystem.model.dto.job.AddJobDto;
import com.cuit.interviewsystem.model.dto.job.JobSearchPageDto;
import com.cuit.interviewsystem.model.entity.JobPosition;
import com.cuit.interviewsystem.model.enums.UserRoleEnum;
import com.cuit.interviewsystem.model.vo.JobPositionVo;
import com.cuit.interviewsystem.model.vo.PageVo;
import com.cuit.interviewsystem.service.CompanyService;
import com.cuit.interviewsystem.service.JobPositionService;
import com.cuit.interviewsystem.service.UserService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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


    @PostMapping("")
    @AuthCheck(roles = {UserRoleEnum.RECRUITER})
    public Result<?> addJobPosition(@Valid AddJobDto addJobDto){
        return jobPositionService.addJobPosition(addJobDto) == 0 ?
                Result.error(ErrorEnum.SYSTEM_ERROR) : Result.success("添加成功");
    }

    @PutMapping("/{id}")
    @AuthCheck(roles = {UserRoleEnum.RECRUITER})
    public Result<?> updateJobPosition(@Valid UpdateJobDto updateJobDto, @PathVariable Long id){
        int i = jobPositionService.updateJobPosition(updateJobDto, id);
    }

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

    @PutMapping("/status/{id}")
    @AuthCheck(roles = {UserRoleEnum.RECRUITER})
    public Result<?> updateJobPositionStatus(@PathVariable Long id, Integer status){
        return jobPositionService.updateJobStatus(id, status) == 0 ?
                Result.error(ErrorEnum.SYSTEM_ERROR) : Result.success("更新成功");
    }

    @GetMapping("/list")
    public Result<PageVo<JobPositionVo>> getJobPositionList(@Valid JobSearchPageDto jobSearchPageDto){
        //TODO 使用xml的sql优化
        Page<JobPosition> res = jobPositionService.getJobPositionList(jobSearchPageDto);
        Page<JobPositionVo> r = new Page<>();
        List<JobPosition> records = res.getRecords();
        List<JobPositionVo> list = new ArrayList<>();
        for (JobPosition record : records) {
            JobPositionVo jobPositionVo = JobPositionVo.objToVo(record);
            if (jobPositionVo != null) {
                jobPositionVo.setCompanyName(companyService.getCompanyById(record.getCompanyId()).getCompanyName());
                jobPositionVo.setHiringManagerName(userService.getById(record.getHiringManagerId()).getUsername());
                list.add(jobPositionVo);
            }
        }
        BeanUtils.copyProperties(res, r, "records");
        r.setRecords(list);
        return Result.success(PageVo.of(r));
    }
}
