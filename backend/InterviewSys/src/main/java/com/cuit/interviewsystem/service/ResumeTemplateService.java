package com.cuit.interviewsystem.service;

import com.cuit.interviewsystem.model.dto.resume.PreviewResumeDto;
import com.cuit.interviewsystem.model.vo.ResumeTemplateVo;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * 简历模板服务接口
 * 用于处理简历预览和导出功能
 */
public interface ResumeTemplateService {

    /**
     * 预览简历（返回HTML页面）
     * @param resumeId 简历ID
     * @param templateId 模板ID
     * @return HTML内容
     */
    String previewResume(Long resumeId, Integer templateId);

    /**
     * 预览未保存的简历（返回HTML页面）
     * @param previewResumeDto 简历数据
     * @param templateId 模板ID
     * @return HTML内容
     */
    String previewUnsavedResume(PreviewResumeDto previewResumeDto, Integer templateId);

    /**
     * 导出简历为PDF
     * @param resumeId 简历ID
     * @param templateId 模板ID
     * @param response HTTP响应对象
     */
    void exportResumeToPdf(Long resumeId, Integer templateId, HttpServletResponse response);

    /**
     * 获取所有简历模板
     * @return 简历模板列表
     */
    List<ResumeTemplateVo> getAllTemplates();
}
