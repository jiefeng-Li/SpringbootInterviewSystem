package com.cuit.interviewsystem.service;

import com.cuit.interviewsystem.model.dto.CompanyCertification4AdminDto;
import com.cuit.interviewsystem.model.dto.CompanyCertificationRecordAddDto;
import com.cuit.interviewsystem.model.entity.CompanyCertificationRecord;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author jiefe
* @description 针对表【t_company_certification_record(公司资质认证记录表)】的数据库操作Service
* @createDate 2026-02-01 16:25:01
*/
public interface CompanyCertificationRecordService extends IService<CompanyCertificationRecord> {
    long addCompanyCertificationRecord(CompanyCertificationRecordAddDto ccrd);

    int reviewCompanyCertification(CompanyCertification4AdminDto dto);
}
