package com.cuit.interviewsystem.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cuit.interviewsystem.model.dto.company.AdminCompanySearchPageDto;
import com.cuit.interviewsystem.model.dto.company.CompanySearchPageDto;
import com.cuit.interviewsystem.model.entity.Company;
import com.cuit.interviewsystem.model.vo.CompanyVo;
import org.apache.ibatis.annotations.Mapper;

/**
* @author jiefe
* @description 针对表【t_company(公司基本信息表)】的数据库操作Mapper
* @createDate 2026-01-28 20:13:34
* @Entity com.cuit.interviewsystem.model.entity.Company
*/
@Mapper
public interface CompanyMapper extends BaseMapper<Company> {
    CompanyVo getCompanyVoById(Long id);

    IPage<CompanyVo> getCompanyVoPage(IPage<CompanyVo> page, CompanySearchPageDto dto);

    IPage<CompanyVo> adminGetCompanyVoPage(Page<CompanyVo> page, AdminCompanySearchPageDto dto);
}




