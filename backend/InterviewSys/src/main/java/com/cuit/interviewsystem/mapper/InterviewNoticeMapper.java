package com.cuit.interviewsystem.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cuit.interviewsystem.model.dto.interview.InterviewNoticeListDto;
import com.cuit.interviewsystem.model.entity.InterviewNotice;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cuit.interviewsystem.model.vo.InterviewNoticeVo;
import org.apache.ibatis.annotations.Mapper;

/**
* @author jiefe
* @description 针对表【t_interview_notice(面试通知表)】的数据库操作Mapper
* @createDate 2026-03-27 03:26:41
* @Entity generator.domain.InterviewNotice
*/
@Mapper
public interface InterviewNoticeMapper extends BaseMapper<InterviewNotice> {

    IPage<InterviewNoticeVo> getNoticeVoList(Page<InterviewNoticeVo> page, InterviewNoticeListDto dto);

    InterviewNoticeVo selectNoticeVoById(Long noticeId);
}




