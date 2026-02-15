package com.cuit.interviewsystem.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cuit.interviewsystem.exception.ErrorEnum;
import com.cuit.interviewsystem.model.dto.UpdateJobDto;
import com.cuit.interviewsystem.model.dto.job.AddJobDto;
import com.cuit.interviewsystem.model.dto.job.JobSearchPageDto;
import com.cuit.interviewsystem.model.entity.JobPosition;
import com.cuit.interviewsystem.model.entity.User;
import com.cuit.interviewsystem.model.enums.JobPositionStatusEnum;
import com.cuit.interviewsystem.model.enums.JobTypeEnum;
import com.cuit.interviewsystem.service.JobPositionService;
import com.cuit.interviewsystem.mapper.JobPositionMapper;
import com.cuit.interviewsystem.utils.JWTUtil;
import com.cuit.interviewsystem.utils.ThrowUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

/**
* @author jiefe
* @description 针对表【t_job_position(职位发布表)】的数据库操作Service实现
* @createDate 2026-02-10 21:44:35
*/
@Service
public class JobPositionServiceImpl extends ServiceImpl<JobPositionMapper, JobPosition>
    implements JobPositionService{
    @Resource
    private JobPositionMapper jobPositionMapper;
    @Resource
    private JWTUtil jwtUtil;

    @Override
    public JobPosition getJobPositionById(Long id) {
        ThrowUtil.throwIfTrue(id == null, ErrorEnum.PARAMS_ERROR);
        return jobPositionMapper.selectById(id);
    }

    @Override
    public Page<JobPosition> getJobPositionList(JobSearchPageDto dto) {
        Page<JobPosition> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<JobPosition> lqw = new LambdaQueryWrapper<JobPosition>()
                .eq(dto.getCompanyId() != null, JobPosition::getCompanyId, dto.getCompanyId())
                .eq(dto.getWorkCity() != null, JobPosition::getWorkCity, dto.getWorkCity())
                .eq(dto.getJobType() != null, JobPosition::getJobType, dto.getJobType())
                .eq(dto.getStatus() != null, JobPosition::getStatus, dto.getStatus())
                .eq(dto.getExperience() != null, JobPosition::getExperience, dto.getExperience())
                .eq(dto.getEducation() != null, JobPosition::getEducation, dto.getEducation())
                .ge(dto.getMinSalary() != null, JobPosition::getMinSalary, dto.getMinSalary())
                .le(dto.getMaxSalary() != null, JobPosition::getMaxSalary, dto.getMaxSalary())
                .eq(JobPosition::getIsDeleted, 0);
        dto.getTags().forEach(tag -> lqw.like(JobPosition::getTags, "\"" + tag + "\""));
        jobPositionMapper.selectPage(page, lqw);
        return page;
    }

    @Override
    public int addJobPosition(AddJobDto addJobDto) {
        User recruiter = jwtUtil.parseLoginUser();
        ThrowUtil.throwIfTrue(recruiter.getCompanyId() == null, ErrorEnum.PARAMS_ERROR, "请先绑定公司");
        JobPosition jobPosition = new JobPosition();
        BeanUtils.copyProperties(addJobDto, jobPosition);
        jobPosition.setCompanyId(recruiter.getCompanyId());

        JobTypeEnum jobTypeEnum = JobTypeEnum.getEnum(addJobDto.getJobType());
        JobPositionStatusEnum status = JobPositionStatusEnum.getEnum(addJobDto.getStatus());
        ThrowUtil.throwIfTrue(jobTypeEnum == null, ErrorEnum.PARAMS_ERROR, "工作性质设置错误");
        ThrowUtil.throwIfTrue(status == null || status.equals(JobPositionStatusEnum.RECRUITING), ErrorEnum.PARAMS_ERROR, "职位状态设置错误");
        if (addJobDto.getTags() != null) {
            jobPosition.setTags(JSONUtil.toJsonStr(addJobDto.getTags()));
        }
        jobPosition.setHiringManagerId(recruiter.getUserId());
        jobPositionMapper.insert(jobPosition);
        return jobPositionMapper.insert(jobPosition);
    }

    @Override
    public int updateJobStatus(Long id, Integer status) {
        JobPosition job = jobPositionMapper.selectById(id);
        User curUser = jwtUtil.parseLoginUser();
        ThrowUtil.throwIfTrue(job == null || job.getIsDeleted() == 1, ErrorEnum.NOT_FOUND_ERROR);
        ThrowUtil.throwIfTrue(!Objects.equals(job.getCompanyId(), curUser.getCompanyId()), ErrorEnum.UNAUTHORIZED);
        JobPositionStatusEnum statusEnum = JobPositionStatusEnum.getEnum(status);
        ThrowUtil.throwIfTrue(statusEnum == null, ErrorEnum.PARAMS_ERROR, "职位状态设置错误");
        ThrowUtil.throwIfTrue(job.getStatus().equals(JobPositionStatusEnum.CLOSED),
                ErrorEnum.PARAMS_ERROR, "职位已关闭，无法修改状态");
        if (job.getStatus().equals(JobPositionStatusEnum.RECRUITING)) {
            ThrowUtil.throwIfTrue(statusEnum.equals(JobPositionStatusEnum.DRAFT),
                    ErrorEnum.PARAMS_ERROR, "职位已发布，无法设置为草稿");
        }
        if (job.getStatus().equals(JobPositionStatusEnum.DRAFT)) {
            ThrowUtil.throwIfTrue(statusEnum.equals(JobPositionStatusEnum.CLOSED),
                    ErrorEnum.PARAMS_ERROR, "职位为草稿，无法设置为关闭");
        }
        job.setStatus(statusEnum);
        job.setUpdateTime(new Date());
        return jobPositionMapper.updateById(job);
    }

    @Override
    public int updateJobPosition(UpdateJobDto updateJobDto, Long id) {
        JobPosition target = jobPositionMapper.selectById(id);
        User curUser = jwtUtil.parseLoginUser();
        ThrowUtil.throwIfTrue(target == null || target.getIsDeleted() == 1, ErrorEnum.NOT_FOUND_ERROR);
        ThrowUtil.throwIfTrue(!Objects.equals(target.getCompanyId(), curUser.getCompanyId()), ErrorEnum.UNAUTHORIZED);
        BeanUtils.copyProperties(updateJobDto, target);
        JobTypeEnum jobTypeEnum = JobTypeEnum.getEnum(updateJobDto.getJobType());
        ThrowUtil.throwIfTrue(jobTypeEnum == null, ErrorEnum.PARAMS_ERROR, "工作性质设置错误");
        if (updateJobDto.getTags() != null) {
            target.setTags(JSONUtil.toJsonStr(updateJobDto.getTags()));
        }
        target.setJobType(jobTypeEnum);
        target.setUpdateTime(new Date());
        return jobPositionMapper.updateById(target);
    }
}




