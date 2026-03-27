package com.cuit.interviewsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cuit.interviewsystem.model.dto.company.AdminCompanySearchPageDto;
import com.cuit.interviewsystem.model.dto.company.CompanyAddDto;
import com.cuit.interviewsystem.model.dto.company.CompanyInfoDto;
import com.cuit.interviewsystem.model.dto.company.CompanySearchPageDto;
import com.cuit.interviewsystem.model.entity.Company;
import com.cuit.interviewsystem.model.vo.CompanyVo;

/**
* @author jiefe
* @description 针对表【t_company(公司基本信息表)】的数据库操作Service
* @createDate 2026-01-28 20:13:34
*/
public interface CompanyService extends IService<Company> {
    CompanyVo getCompanyVoById(Long id);
    Company getCompanyById(Long id);

    int deleteCompanyById(Long id);

    int deregisterCompanyById(Long id);

    String addOneCompany(CompanyAddDto cad);

    int updateCompanyById(Long id, CompanyInfoDto cid);

    int updateCompanyStatusBySysAdmin(Long id, Integer status);

    Page<CompanyVo> getCompanyList(CompanySearchPageDto dto);

    Page<CompanyVo> adminGetCompanyList(AdminCompanySearchPageDto dto);
}
