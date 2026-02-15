package com.cuit.interviewsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cuit.interviewsystem.model.dto.UpdateJobDto;
import com.cuit.interviewsystem.model.dto.job.AddJobDto;
import com.cuit.interviewsystem.model.dto.job.JobSearchPageDto;
import com.cuit.interviewsystem.model.entity.JobPosition;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author jiefe
* @description 针对表【t_job_position(职位发布表)】的数据库操作Service
* @createDate 2026-02-10 21:44:35
*/
public interface JobPositionService extends IService<JobPosition> {

    JobPosition getJobPositionById(Long id);

    Page<JobPosition> getJobPositionList(JobSearchPageDto jobSearchPageDto);

    int addJobPosition(AddJobDto addJobDto);

    int updateJobStatus(Long id, Integer status);

    int updateJobPosition(UpdateJobDto updateJobDto, Long id);
}
