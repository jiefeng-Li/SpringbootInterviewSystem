package com.cuit.interviewsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cuit.interviewsystem.model.dto.job.JobSearchPageDto;
import com.cuit.interviewsystem.model.entity.JobPosition;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cuit.interviewsystem.model.vo.JobPositionVo;

/**
* @author jiefe
* @description 针对表【t_job_position(职位发布表)】的数据库操作Service
* @createDate 2026-02-10 21:44:35
*/
public interface JobPositionService extends IService<JobPosition> {

    JobPosition getJobPositionById(Long id);

    Page<JobPositionVo> getJobPositionList(JobSearchPageDto jobSearchPageDto);
}
