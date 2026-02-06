package com.cuit.interviewsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cuit.interviewsystem.model.dto.AdminReviewCertificationDto;
import com.cuit.interviewsystem.model.dto.CertificationRecordPageDto;
import com.cuit.interviewsystem.model.dto.CompanyCertificationRecordAddDto;
import com.cuit.interviewsystem.model.entity.CompanyCertificationRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cuit.interviewsystem.model.vo.CompanyCertificationRecordVo;

/**
* @author jiefe
* @description 针对表【t_company_certification_record(公司资质认证记录表)】的数据库操作Service
* @createDate 2026-02-01 16:25:01
*/
public interface CompanyCertificationRecordService extends IService<CompanyCertificationRecord> {
    long addCompanyCertificationRecord(CompanyCertificationRecordAddDto ccrd);

    int reviewCompanyCertification(AdminReviewCertificationDto dto);

    Page<CompanyCertificationRecordVo> getRecords(CertificationRecordPageDto dto);

    int deleteRecordById(Long id);
}
