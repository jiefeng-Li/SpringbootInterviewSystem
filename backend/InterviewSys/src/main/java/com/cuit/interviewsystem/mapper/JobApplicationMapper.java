package com.cuit.interviewsystem.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cuit.interviewsystem.model.dto.jobApplication.OwnJobApplicationPageDto;
import com.cuit.interviewsystem.model.dto.jobApplication.JobApplicationPageDto;
import com.cuit.interviewsystem.model.entity.JobApplication;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cuit.interviewsystem.model.vo.JobApplicationVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
* @author jiefe
* @description 针对表【t_job_application(简历投递记录表)】的数据库操作Mapper
* @createDate 2026-03-01 21:08:39
* @Entity {@link com.cuit.interviewsystem.model.entity.JobApplication}
*/
@Mapper
public interface JobApplicationMapper extends BaseMapper<JobApplication> {

    IPage<JobApplicationVo> getJobApplicationPage(Page<JobApplicationVo> page, JobApplicationPageDto dto);


    @Select("select t.*, job.title as job_title " +
            "from t_job_application t " +
            "left join t_job_position job on job.id = t.resume_id " +
            "where t.id = #{id}")
    JobApplicationVo getJobApplicationById(Long id);

    IPage<JobApplicationVo> getOwnJobApplicationPage(Page<JobApplicationVo> page, OwnJobApplicationPageDto pageDto);
}