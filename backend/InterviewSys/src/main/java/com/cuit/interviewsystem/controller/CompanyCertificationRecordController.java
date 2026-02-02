package com.cuit.interviewsystem.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cuit.interviewsystem.annotation.AuthCheck;
import com.cuit.interviewsystem.common.Result;
import com.cuit.interviewsystem.model.dto.AdminReviewCertificationDto;
import com.cuit.interviewsystem.model.dto.CertificationRecordPageDto;
import com.cuit.interviewsystem.model.dto.CompanyCertificationRecordAddDto;
import com.cuit.interviewsystem.model.entity.CompanyCertificationRecord;
import com.cuit.interviewsystem.model.enums.CompanyCertificationStatusEnum;
import com.cuit.interviewsystem.model.enums.UserRoleEnum;
import com.cuit.interviewsystem.service.CompanyCertificationRecordService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("certification")
public class CompanyCertificationRecordController {
    @Resource
    private CompanyCertificationRecordService recordService;

    /**
     * 状态(0待审,1通过,2驳回)
     * @return
     */
    @GetMapping("/status")
    public Result<List<?>> getCompanyCertificationStatus() {
        record certificationRecord(Integer status, String text){}
        List<certificationRecord> res = new ArrayList<>();
        for (CompanyCertificationStatusEnum status : CompanyCertificationStatusEnum.values())
            res.add(new certificationRecord(status.getStatus(), status.getText()));
        return Result.success(res);
    }

    /**
     * 提交企业认证
     * @param ccrad
     * @return
     */
    @PostMapping
    @AuthCheck(roles = {UserRoleEnum.COMP_ADMIN})
    public Result<Long> companyCertification(CompanyCertificationRecordAddDto ccrad) {
        long recordId = recordService.addCompanyCertificationRecord(ccrad);
        return Result.success(recordId, "提交成功");
    }

    /**
     * 管理员审核企业认证
     * @param id
     * @return
     */
    @PutMapping("/{id}")
    @AuthCheck(roles = {UserRoleEnum.SYS_ADMIN})
    public Result<String> reviewCompanyCertification(@PathVariable Long id, @RequestBody AdminReviewCertificationDto dto) {
        recordService.reviewCompanyCertification(dto);
        return Result.success("提交成功");
    }

    @GetMapping("/list")
    @AuthCheck(roles = {UserRoleEnum.SYS_ADMIN})
    public Result<?> companyCertificationList(CertificationRecordPageDto dto) {
        Page<CompanyCertificationRecord> page = recordService.getRecords(dto);
        return Result.success();
    }
}
