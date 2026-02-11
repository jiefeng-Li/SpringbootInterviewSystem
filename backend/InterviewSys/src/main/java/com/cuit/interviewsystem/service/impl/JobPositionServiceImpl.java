package com.cuit.interviewsystem.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cuit.interviewsystem.exception.ErrorEnum;
import com.cuit.interviewsystem.model.dto.job.JobSearchPageDto;
import com.cuit.interviewsystem.model.entity.JobPosition;
import com.cuit.interviewsystem.model.vo.JobPositionVo;
import com.cuit.interviewsystem.service.JobPositionService;
import com.cuit.interviewsystem.mapper.JobPositionMapper;
import com.cuit.interviewsystem.utils.ThrowUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

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

    @Override
    public JobPosition getJobPositionById(Long id) {
        ThrowUtil.throwIfTrue(id == null, ErrorEnum.PARAMS_ERROR);
        return jobPositionMapper.selectById(id);
    }

    @Override
    public Page<JobPositionVo> getJobPositionList(JobSearchPageDto jobSearchPageDto) {
        Page<JobPositionVo> page = new Page<>(jobSearchPageDto.getPageNum(), jobSearchPageDto.getPageSize());
        jobPositionMapper.getJobPositionList(page, jobSearchPageDto);
        return null;·
    }
}




