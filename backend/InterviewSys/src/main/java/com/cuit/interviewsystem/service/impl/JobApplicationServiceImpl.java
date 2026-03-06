package com.cuit.interviewsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cuit.interviewsystem.exception.ErrorEnum;
import com.cuit.interviewsystem.mapper.JobPositionMapper;
import com.cuit.interviewsystem.mapper.ResumeMapper;
import com.cuit.interviewsystem.model.dto.UpdateJobApplicationDto;
import com.cuit.interviewsystem.model.dto.jobApplication.AddJobApplicationDto;
import com.cuit.interviewsystem.model.dto.jobApplication.JobApplicationPageDto;
import com.cuit.interviewsystem.model.entity.JobApplication;
import com.cuit.interviewsystem.model.entity.JobPosition;
import com.cuit.interviewsystem.model.enums.JobApplicationStatusEnum;
import com.cuit.interviewsystem.model.enums.JobPositionStatusEnum;
import com.cuit.interviewsystem.model.vo.JobApplicationVo;
import com.cuit.interviewsystem.model.vo.ResumeVo;
import com.cuit.interviewsystem.service.JobApplicationService;
import com.cuit.interviewsystem.mapper.JobApplicationMapper;
import com.cuit.interviewsystem.utils.ThrowUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

/**
* @author jiefe
* @description 针对表【t_job_application(简历投递记录表)】的数据库操作Service实现
* @createDate 2026-03-01 21:08:39
*/
@Service
public class JobApplicationServiceImpl extends ServiceImpl<JobApplicationMapper, JobApplication>
    implements JobApplicationService{

    @Resource
    private JobApplicationMapper jobApplicationMapper;
    @Resource
    private JobPositionMapper jobPositionMapper;
    @Resource
    private ResumeMapper resumeMapper;

    @Override
    public JobApplicationVo getJobApplicationById(Long id) {
        ThrowUtil.throwIfTrue(id == null, ErrorEnum.PARAMS_ERROR, "投递记录ID不能为空");
        JobApplicationVo res = jobApplicationMapper.getJobApplicationById(id);
        res.setResume(resumeMapper.selectVoById(res.getResumeId()));
        return res;
    }

    @Override
    public Page<JobApplicationVo> getJobApplicationList(JobApplicationPageDto jobApplicationPageDto) {
        Page<JobApplicationVo> page = new Page<>(jobApplicationPageDto.getPageNum(), jobApplicationPageDto.getPageSize());
        jobApplicationMapper.getJobApplicationPage(page, jobApplicationPageDto);
        for (JobApplicationVo apply : page.getRecords()) {
            ResumeVo resume = resumeMapper.selectVoById(apply.getResumeId());
            if (resume != null)
                apply.setResume(resume);
        }
        return page;
    }

    @Override
    public void addJobApplication(AddJobApplicationDto addJobApplicationDto) {
        JobPosition job = jobPositionMapper.selectById(addJobApplicationDto.getJobPositionId());
        ThrowUtil.throwIfTrue(job == null || job.getIsDeleted() == 1, ErrorEnum.PARAMS_ERROR, "职位不存在");
        ThrowUtil.throwIfTrue(job.getStatus() != JobPositionStatusEnum.RECRUITING, ErrorEnum.PARAMS_ERROR, "职位不在招聘状态");
        ThrowUtil.throwIfTrue(!Objects.equals(job.getCompanyId(), addJobApplicationDto.getCompanyId()), ErrorEnum.PARAMS_ERROR, "公司ID错误");
        JobApplication jobApplication = new JobApplication();
        BeanUtils.copyProperties(addJobApplicationDto, jobApplication);
        jobApplication.setStatus(JobApplicationStatusEnum.PENDING.getStatus());
        jobApplicationMapper.insert(jobApplication);
    }

    @Override
    public int deleteJobApplication(Long id) {
        // TODO
        return 0;
    }

    @Override
    public int updateJobApplicationStatus(UpdateJobApplicationDto jobApplication) {
        JobApplication apply = jobApplicationMapper.selectById(jobApplication.getId());
        ThrowUtil.throwIfTrue(apply == null || apply.getIsDeleted() == 1, ErrorEnum.PARAMS_ERROR, "投递记录不存在");
        if (jobApplication.getStatus() != null) {
            ThrowUtil.throwIfTrue(JobApplicationStatusEnum.checkStatus(apply.getStatus(), jobApplication.getStatus()),
                    ErrorEnum.PARAMS_ERROR, "状态错误");
            apply.setStatus(jobApplication.getStatus());
        }
        apply.setRemarks(jobApplication.getRemarks());
        apply.setUpdateTime(new Date());
        return jobApplicationMapper.update(apply, new LambdaUpdateWrapper<>(JobApplication.class)
                .eq(JobApplication::getId, jobApplication.getId()));
    }
}




