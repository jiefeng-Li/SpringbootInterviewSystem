package com.cuit.interviewsystem.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cuit.interviewsystem.exception.ErrorEnum;
import com.cuit.interviewsystem.model.dto.interview.InterviewNoticeAddDto;
import com.cuit.interviewsystem.model.dto.interview.InterviewNoticeListDto;
import com.cuit.interviewsystem.model.entity.InterviewNotice;
import com.cuit.interviewsystem.model.vo.InterviewNoticeVo;
import com.cuit.interviewsystem.service.InterviewNoticeService;
import com.cuit.interviewsystem.mapper.InterviewNoticeMapper;
import com.cuit.interviewsystem.utils.ThrowUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
* @author jiefe
* @description 针对表【t_interview_notice(面试通知表)】的数据库操作Service实现
* @createDate 2026-03-27 03:26:41
*/
@Service
public class InterviewNoticeServiceImpl extends ServiceImpl<InterviewNoticeMapper, InterviewNotice>
    implements InterviewNoticeService{

    @Resource
    private InterviewNoticeMapper interviewNoticeMapper;

    @Override
    public void addInterviewNotice(InterviewNoticeAddDto dto) {
        InterviewNotice data = new InterviewNotice();
        BeanUtils.copyProperties(dto, data);
        interviewNoticeMapper.insert(data);
    }

    @Override
    public Page<InterviewNoticeVo> getInterviewNoticeList(InterviewNoticeListDto dto) {
        Page<InterviewNoticeVo> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        interviewNoticeMapper.getNoticeVoList(page, dto);
        return page;
    }

    @Override
    public InterviewNoticeVo getNoticeVoById(Long noticeId) {
        ThrowUtil.throwIfTrue(noticeId == null, ErrorEnum.PARAMS_ERROR, "面试通知id不能为空");
        return interviewNoticeMapper.selectNoticeVoById(noticeId);
    }
}




