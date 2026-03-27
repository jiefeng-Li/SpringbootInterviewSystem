package com.cuit.interviewsystem.service.impl;

import com.cuit.interviewsystem.constant.ResumeTemplate;
import com.cuit.interviewsystem.exception.BusinessException;
import com.cuit.interviewsystem.exception.ErrorEnum;
import com.cuit.interviewsystem.model.dto.resume.PreviewResumeDto;
import com.cuit.interviewsystem.model.vo.ResumeEducationVo;
import com.cuit.interviewsystem.model.vo.ResumeExperienceVo;
import com.cuit.interviewsystem.model.vo.ResumeProjectVo;
import com.cuit.interviewsystem.model.vo.ResumeTemplateVo;
import com.cuit.interviewsystem.model.vo.ResumeVo;
import com.cuit.interviewsystem.service.ResumeService;
import com.cuit.interviewsystem.service.ResumeTemplateService;
import com.cuit.interviewsystem.utils.ThrowUtil;
import org.springframework.beans.BeanUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import com.lowagie.text.pdf.BaseFont;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

/**
 * 简历模板服务实现类
 * 用于处理简历预览和导出功能
 */
@Service
@Slf4j
public class ResumeTemplateServiceImpl implements ResumeTemplateService {

    @Resource
    private ResumeService resumeService;

    @Resource
    private TemplateEngine templateEngine;

    @Override
    public String previewResume(Long resumeId, Integer templateId) {
        ThrowUtil.throwIfTrue(resumeId == null, ErrorEnum.PARAMS_ERROR, "简历ID不能为空");

        // 获取简历数据
        ResumeVo resume = resumeService.getResumeById(resumeId);
        ThrowUtil.throwIfTrue(resume == null, ErrorEnum.PARAMS_ERROR, "简历不存在");

        // 优先使用接口传入的 templateId，便于导出时切换模板；未传时再回退到简历自身模板
        Integer finalTemplateId = templateId != null ? templateId : resume.getTemplateId();

        return renderResumeHtml(resume, finalTemplateId);
    }

    private String renderResumeHtml(ResumeVo resume, Integer templateId) {
        resume.setEducations(resume.getEducations() == null ? Collections.emptyList() : resume.getEducations());
        resume.setExperiences(resume.getExperiences() == null ? Collections.emptyList() : resume.getExperiences());
        resume.setProjects(resume.getProjects() == null ? Collections.emptyList() : resume.getProjects());

        log.info("渲染简历模板: resumeId={}, templateId={}, eduSize={}, expSize={}, projectSize={}",
                resume.getId(),
                templateId,
                resume.getEducations().size(),
                resume.getExperiences().size(),
                resume.getProjects().size());

        if (!resume.getEducations().isEmpty()) {
            ResumeEducationVo firstEdu = resume.getEducations().get(0);
            log.info("首条教育: school={}, major={}, degree={}", firstEdu.getSchool(), firstEdu.getMajor(), firstEdu.getDegree());
        }
        if (!resume.getExperiences().isEmpty()) {
            ResumeExperienceVo firstExp = resume.getExperiences().get(0);
            log.info("首条工作: company={}, position={}", firstExp.getCompany(), firstExp.getPosition());
        }
        if (!resume.getProjects().isEmpty()) {
            ResumeProjectVo firstProject = resume.getProjects().get(0);
            log.info("首条项目: name={}", firstProject.getName());
        }

        // 获取模板路径
        String templatePath = ResumeTemplate.getTemplatePathById(templateId);

        // 创建Thymeleaf上下文
        Context context = new Context();
        context.setVariable("resume", resume);

        // 使用模板引擎渲染HTML
        return templateEngine.process(templatePath, context);
    }

    @Override
    public String previewUnsavedResume(PreviewResumeDto previewResumeDto, Integer templateId) {
        ThrowUtil.throwIfTrue(templateId == null, ErrorEnum.PARAMS_ERROR, "模板ID不能为空");

        // 将PreviewResumeDto转换为ResumeVo
        ResumeVo resume = new ResumeVo();
        BeanUtils.copyProperties(previewResumeDto, resume);

        // 转换教育经历
        if (previewResumeDto.getEducations() != null) {
            List<ResumeEducationVo> educationVos = previewResumeDto.getEducations().stream()
                    .map(edu -> {
                        ResumeEducationVo vo = new ResumeEducationVo();
                        BeanUtils.copyProperties(edu, vo);
                        return vo;
                    })
                    .toList();
            resume.setEducations(educationVos);
        }

        // 转换工作经历
        if (previewResumeDto.getExperiences() != null) {
            List<ResumeExperienceVo> experienceVos = previewResumeDto.getExperiences().stream()
                    .map(exp -> {
                        ResumeExperienceVo vo = new ResumeExperienceVo();
                        BeanUtils.copyProperties(exp, vo);
                        return vo;
                    })
                    .toList();
            resume.setExperiences(experienceVos);
        }

        // 转换项目经历
        if (previewResumeDto.getProjects() != null) {
            List<ResumeProjectVo> projectVos = previewResumeDto.getProjects().stream()
                    .map(proj -> {
                        ResumeProjectVo vo = new ResumeProjectVo();
                        BeanUtils.copyProperties(proj, vo);
                        return vo;
                    })
                    .toList();
            resume.setProjects(projectVos);
        }

        // 获取模板路径
        String templatePath = ResumeTemplate.getTemplatePathById(templateId);

        // 创建Thymeleaf上下文
        Context context = new Context();
        context.setVariable("resume", resume);

        // 使用模板引擎渲染HTML
        return templateEngine.process(templatePath, context);
    }

    @Override
    public void exportResumeToPdf(Long resumeId, Integer templateId, HttpServletResponse response) {
        ThrowUtil.throwIfTrue(resumeId == null, ErrorEnum.PARAMS_ERROR, "简历ID不能为空");

        try {
            ResumeVo resume = resumeService.getResumeById(resumeId);
            ThrowUtil.throwIfTrue(resume == null, ErrorEnum.PARAMS_ERROR, "简历不存在");

            Integer finalTemplateId = templateId != null ? templateId : resume.getTemplateId();
            String xhtmlContent = buildExportXhtml(resume, finalTemplateId, resumeId);
            byte[] pdfBytes = generatePdfBytes(xhtmlContent);
            writePdfToResponse(response, pdfBytes, resumeId);
        } catch (IOException e) {
            log.error("导出PDF失败", e);
            throw new BusinessException(ErrorEnum.SYSTEM_ERROR, "导出PDF失败");
        } catch (Exception e) {
            log.error("生成PDF失败", e);
            throw new BusinessException(ErrorEnum.SYSTEM_ERROR, "生成PDF失败");
        }
    }

    private String buildExportXhtml(ResumeVo resume, Integer finalTemplateId, Long resumeId) {
        String htmlContent = renderResumeHtml(resume, finalTemplateId);
        String xhtmlContent = normalizeToXhtml(htmlContent);
        log.info("导出简历PDF: resumeId={}, templateId={}, htmlLen={}, normalizedLen={}",
                resumeId, finalTemplateId, htmlContent.length(), xhtmlContent.length());
        return xhtmlContent;
    }

    private byte[] generatePdfBytes(String xhtmlContent) throws Exception {
        ITextRenderer renderer = new ITextRenderer();
        registerPdfFonts(renderer);
        renderer.setDocumentFromString(xhtmlContent);
        renderer.layout();

        byte[] pdfBytes;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            renderer.createPDF(baos);
            pdfBytes = baos.toByteArray();
        }

        if (pdfBytes.length == 0) {
            throw new BusinessException(ErrorEnum.SYSTEM_ERROR, "生成PDF失败：内容为空");
        }
        return pdfBytes;
    }

    private void writePdfToResponse(HttpServletResponse response, byte[] pdfBytes, Long resumeId) throws IOException {
        response.setContentType("application/pdf");
        response.setCharacterEncoding("UTF-8");
        response.setContentLength(pdfBytes.length);
        String fileName = new String(("resume_" + resumeId + ".pdf").getBytes(StandardCharsets.UTF_8),
                StandardCharsets.ISO_8859_1);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        try (OutputStream outputStream = response.getOutputStream()) {
            outputStream.write(pdfBytes);
            outputStream.flush();
        }
    }

    /**
     * 对模板输出做最小清洗，避免 Flying Saucer 以 XML 解析时因非法字符或 HTML 实体失败。
     */
    private String normalizeToXhtml(String htmlContent) {
        String content = htmlContent == null ? "" : htmlContent;
        // 1) 真实 BOM（U+FEFF）
        if (!content.isEmpty() && content.charAt(0) == '\uFEFF') {
            content = content.substring(1);
        }

        // 2) 被错误解码后的 UTF-8 BOM 三字符 "ï»¿"
        if (content.startsWith("ï»¿")) {
            content = content.substring(3);
        }

        // 3) 移除 XML 非法控制字符
        content = content.replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]", "");

        // 3.1) XHTML 解析器不识别 HTML 专有命名实体，转为数值实体
        content = content.replace("&nbsp;", "&#160;");

        // 4) 统一从 <html ...> 起始，规避声明区异常字符导致 TrAX 解析失败
        int htmlTagIndex = content.toLowerCase().indexOf("<html");
        if (htmlTagIndex >= 0) {
            if (htmlTagIndex > 0) {
                log.warn("检测到模板输出前存在前导字符，已截断到<html>，前导长度={}", htmlTagIndex);
            }
            content = content.substring(htmlTagIndex);
        } else {
            int firstTagIndex = content.indexOf('<');
            if (firstTagIndex > 0) {
                log.warn("未检测到<html>，已截断到首个标签，前导长度={}", firstTagIndex);
                content = content.substring(firstTagIndex);
            }
        }

        // 5) 确保 html 根标签带 XHTML 命名空间
        if (content.toLowerCase().startsWith("<html") && !content.contains("xmlns=\"http://www.w3.org/1999/xhtml\"")) {
            content = content.replaceFirst("(?i)<html", "<html xmlns=\"http://www.w3.org/1999/xhtml\"");
        }

        return content;
    }

    /**
     * 注册常见中文字体，避免 PDF 导出时中文内容丢失。
     */
    private void registerPdfFonts(ITextRenderer renderer) {
        String[] fontPaths = new String[] {
                "C:/Windows/Fonts/msyh.ttc",
                "C:/Windows/Fonts/simsun.ttc",
                "C:/Windows/Fonts/simhei.ttf",
                "C:/Windows/Fonts/msyh.ttf"
        };

        boolean registered = false;
        for (String fontPath : fontPaths) {
            try {
                File file = new File(fontPath);
                if (!file.exists()) {
                    continue;
                }
                renderer.getFontResolver().addFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                registered = true;
            } catch (Exception e) {
                log.warn("注册PDF字体失败: {}", fontPath);
            }
        }

        if (!registered) {
            log.warn("未找到可用中文字体，导出PDF可能出现中文缺失");
        }
    }

    @Override
    public List<ResumeTemplateVo> getAllTemplates() {
        return java.util.Arrays.stream(ResumeTemplate.values())
                .map(template -> new ResumeTemplateVo(
                        template.getId(),
                        template.getName()
                ))
                .collect(java.util.stream.Collectors.toList());
    }
}
