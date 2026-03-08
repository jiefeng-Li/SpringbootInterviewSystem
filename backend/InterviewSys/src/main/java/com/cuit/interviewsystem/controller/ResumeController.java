package com.cuit.interviewsystem.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cuit.interviewsystem.annotation.AuthCheck;
import com.cuit.interviewsystem.common.Result;
import com.cuit.interviewsystem.model.dto.resume.AddResumeDto;
import com.cuit.interviewsystem.model.dto.resume.ResumePageDto;
import com.cuit.interviewsystem.model.vo.PageVo;
import com.cuit.interviewsystem.model.vo.ResumeVo;
import com.cuit.interviewsystem.service.ResumeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/resume")
@Tag(name = "简历管理")
public class ResumeController {
    @Resource
    private ResumeService resumeService;

    @GetMapping("/list")
    @AuthCheck
    public Result<PageVo<ResumeVo>> getResumesPageByUserId(@Valid ResumePageDto resumePageDto) {
        Page<ResumeVo> page = resumeService.getResumesByUserId(resumePageDto);
        PageVo<ResumeVo> res = PageVo.of(page);
        return Result.success(res);
    }

    @GetMapping("/{id}")
    @AuthCheck
    public Result<ResumeVo> getResumeById(@PathVariable Long id) {
        ResumeVo res = resumeService.getResumeById(id);
        return Result.success(res);
    }

    @PostMapping("")
    @AuthCheck
    public Result<Integer> addResume(@Valid AddResumeDto resume) {
        resumeService.addResume(resume);
        return Result.success(null, "添加成功");
    }

    @DeleteMapping("/{id}")
    @AuthCheck
    public Result<Integer> deleteResume(@PathVariable Long id) {
        resumeService.deleteResume(id);
        return Result.success(null, "删除成功");
    }

    @PutMapping("/{id}")
    @AuthCheck
    public Result<Integer> updateResume(@Valid AddResumeDto resume, @PathVariable Long id) {
        resumeService.updateResume(resume, id);
        return Result.success(null, "更新成功");
    }

    @PutMapping("/default/{id}")
    @AuthCheck
    public Result<?> setDefaultResume(@PathVariable Long id) {
        resumeService.setDefault(id);
        return Result.success(null, "设置成功");
    }

    //TODO: 简历上传
    //TODO: 简历模板
}
