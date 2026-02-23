package com.cuit.interviewsystem.service;

import com.cuit.interviewsystem.model.dto.resume.AddResumeDto;
import com.cuit.interviewsystem.model.entity.Resume;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author jiefe
* @description 针对表【t_resume】的数据库操作Service
* @createDate 2026-02-23 21:03:31
*/
public interface ResumeService extends IService<Resume> {
    int addResume(AddResumeDto resume);
}
