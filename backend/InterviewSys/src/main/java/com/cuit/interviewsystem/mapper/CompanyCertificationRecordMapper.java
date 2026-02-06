package com.cuit.interviewsystem.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cuit.interviewsystem.model.dto.CertificationRecordPageDto;
import com.cuit.interviewsystem.model.entity.CompanyCertificationRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cuit.interviewsystem.model.vo.CompanyCertificationRecordVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author jiefe
* @description 针对表【t_company_certification_record(公司资质认证记录表)】的数据库操作Mapper
* @createDate 2026-02-01 16:25:01
* @Entity generator.domain.CompanyCertificationRecord
*/
@Mapper
public interface CompanyCertificationRecordMapper extends BaseMapper<CompanyCertificationRecord> {
    IPage<CompanyCertificationRecordVo> getRecord(IPage<?> page, CertificationRecordPageDto dto);
}

