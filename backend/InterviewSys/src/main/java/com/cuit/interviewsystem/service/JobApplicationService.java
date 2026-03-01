package com.cuit.interviewsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cuit.interviewsystem.model.dto.jobApplication.JobApplicationPageDto;
import com.cuit.interviewsystem.model.entity.JobApplication;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cuit.interviewsystem.model.vo.JobApplicationVo;

/**
* @author jiefe
* @description 针对表【t_job_application(简历投递记录表)】的数据库操作Service
* @createDate 2026-03-01 21:08:39
*/
public interface JobApplicationService extends IService<JobApplication> {

    Page<JobApplicationVo> getJobApplicationList(JobApplicationPageDto jobApplicationPageDto);
}
