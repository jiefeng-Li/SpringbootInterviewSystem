package com.cuit.interviewsystem.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cuit.interviewsystem.model.dto.jobApplication.JobApplicationPageDto;
import com.cuit.interviewsystem.model.entity.JobApplication;
import com.cuit.interviewsystem.model.vo.JobApplicationVo;
import com.cuit.interviewsystem.service.JobApplicationService;
import com.cuit.interviewsystem.mapper.JobApplicationMapper;
import org.springframework.stereotype.Service;

/**
* @author jiefe
* @description 针对表【t_job_application(简历投递记录表)】的数据库操作Service实现
* @createDate 2026-03-01 21:08:39
*/
@Service
public class JobApplicationServiceImpl extends ServiceImpl<JobApplicationMapper, JobApplication>
    implements JobApplicationService{

    @Override
    public Page<JobApplicationVo> getJobApplicationList(JobApplicationPageDto jobApplicationPageDto) {
        return null;
    }
}




