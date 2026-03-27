package com.cuit.interviewsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cuit.interviewsystem.model.dto.jobApplication.OwnJobApplicationPageDto;
import com.cuit.interviewsystem.model.dto.jobApplication.UpdateJobApplicationBatchDto;
import com.cuit.interviewsystem.model.dto.jobApplication.UpdateJobApplicationDto;
import com.cuit.interviewsystem.model.dto.jobApplication.AddJobApplicationDto;
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

    void addJobApplication(AddJobApplicationDto addJobApplicationDto);

    int deleteJobApplication(Long id);

    int updateJobApplicationStatus(UpdateJobApplicationDto jobApplication);

    int updateJobApplicationStatusBatch(UpdateJobApplicationBatchDto dto);

    JobApplicationVo getJobApplicationById(Long id);

    Page<JobApplicationVo> getOwnApplicationList(OwnJobApplicationPageDto pageDto);
}
