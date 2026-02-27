package com.cuit.interviewsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cuit.interviewsystem.model.dto.resume.ResumePageDto;
import com.cuit.interviewsystem.model.dto.resume.AddResumeDto;
import com.cuit.interviewsystem.model.entity.Resume;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cuit.interviewsystem.model.vo.ResumeVo;

/**
* @author jiefe
* @description 针对表【t_resume】的数据库操作Service
* @createDate 2026-02-23 21:03:31
*/
public interface ResumeService extends IService<Resume> {
    int addResume(AddResumeDto resume);

    Page<ResumeVo> getResumesByUserId(ResumePageDto resumePageDto);

    void deleteResume(Long id);

    void setDefault(Long id);

    ResumeVo getResumeById(Long id);

    void updateResume(AddResumeDto resume, Long id);
}
