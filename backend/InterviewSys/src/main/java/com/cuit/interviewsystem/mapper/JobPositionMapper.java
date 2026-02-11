package com.cuit.interviewsystem.mapper;

import com.cuit.interviewsystem.model.entity.JobPosition;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author jiefe
* @description 针对表【t_job_position(职位发布表)】的数据库操作Mapper
* @createDate 2026-02-10 21:44:35
* @Entity com.cuit.interviewsystem.model.entity.JobPosition
*/
@Mapper
public interface JobPositionMapper extends BaseMapper<JobPosition> {

}




