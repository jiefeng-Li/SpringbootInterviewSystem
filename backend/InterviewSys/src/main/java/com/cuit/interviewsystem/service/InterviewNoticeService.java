package com.cuit.interviewsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cuit.interviewsystem.model.dto.interview.InterviewNoticeAddDto;
import com.cuit.interviewsystem.model.dto.interview.InterviewNoticeListDto;
import com.cuit.interviewsystem.model.entity.InterviewNotice;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cuit.interviewsystem.model.vo.InterviewNoticeVo;

/**
* @author jiefe
* @description 针对表【t_interview_notice(面试通知表)】的数据库操作Service
* @createDate 2026-03-27 03:26:41
*/
public interface InterviewNoticeService extends IService<InterviewNotice> {
    void addInterviewNotice(InterviewNoticeAddDto dto);

    Page<InterviewNoticeVo> getInterviewNoticeList(InterviewNoticeListDto dto);

    InterviewNoticeVo getNoticeVoById(Long noticeId);
}
