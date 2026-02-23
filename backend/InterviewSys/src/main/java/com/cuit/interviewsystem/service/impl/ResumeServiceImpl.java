package com.cuit.interviewsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cuit.interviewsystem.mapper.ResumeEducationMapper;
import com.cuit.interviewsystem.mapper.ResumeExperienceMapper;
import com.cuit.interviewsystem.mapper.ResumeProjectMapper;
import com.cuit.interviewsystem.model.dto.resume.AddResumeDto;
import com.cuit.interviewsystem.model.entity.Resume;
import com.cuit.interviewsystem.service.ResumeService;
import com.cuit.interviewsystem.mapper.ResumeMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
* @author jiefe
* @description 针对表【t_resume】的数据库操作Service实现
* @createDate 2026-02-23 21:03:31
*/
@Service
public class ResumeServiceImpl extends ServiceImpl<ResumeMapper, Resume>
    implements ResumeService{
    @Resource
    private ResumeMapper resumeMapper;
    @Resource
    private ResumeEducationMapper educationMapper;
    @Resource
    private ResumeExperienceMapper experienceMapper;
    @Resource
    private ResumeProjectMapper projectMapper;

    @Override
    public int addResume(AddResumeDto resume) {
        return 0;
    }
}




