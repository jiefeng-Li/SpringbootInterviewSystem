package com.cuit.interviewsystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cuit.interviewsystem.common.Result;
import com.cuit.interviewsystem.model.dto.interview.InterviewNoticeAddDto;
import com.cuit.interviewsystem.model.dto.interview.InterviewNoticeListDto;
import com.cuit.interviewsystem.model.vo.InterviewNoticeVo;
import com.cuit.interviewsystem.model.vo.PageVo;
import com.cuit.interviewsystem.service.InterviewNoticeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * 面试通知Controller
 */
@RestController
@RequestMapping("/interviewNotice")
public class InterviewNoticeController {

    @Resource
    private InterviewNoticeService interviewNoticeService;

    /**
     * 添加面试通知
     * @param dto 面试通知添加DTO
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result<Void> addInterviewNotice(@RequestBody InterviewNoticeAddDto dto) {
        interviewNoticeService.addInterviewNotice(dto);
        return Result.success();
    }

    /**
     * 获取面试通知列表
     * @param dto 查询条件DTO
     * @return 面试通知分页列表
     */
    @GetMapping("/list")
    public Result<PageVo<InterviewNoticeVo>> getInterviewNoticeList(InterviewNoticeListDto dto) {
        Page<InterviewNoticeVo> page = interviewNoticeService.getInterviewNoticeList(dto);
        return Result.success(PageVo.of(page));
    }

    /**
     * 获取面试通知详情
     * @param noticeId 面试通知ID
     * @return 面试通知详情
     */
    @GetMapping
    public Result<InterviewNoticeVo> getNoticeById(Long noticeId) {
        return Result.success(interviewNoticeService.getNoticeVoById(noticeId));
    }
}
