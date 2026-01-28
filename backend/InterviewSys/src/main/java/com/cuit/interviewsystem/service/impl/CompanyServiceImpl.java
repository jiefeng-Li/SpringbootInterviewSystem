package com.cuit.interviewsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cuit.interviewsystem.exception.ErrorEnum;
import com.cuit.interviewsystem.model.entity.Company;
import com.cuit.interviewsystem.service.CompanyService;
import com.cuit.interviewsystem.mapper.CompanyMapper;
import com.cuit.interviewsystem.utils.ThrowUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
* @author jiefe
* @description 针对表【t_company(公司基本信息表)】的数据库操作Service实现
* @createDate 2026-01-28 20:13:34
*/
@Service
@Slf4j
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, Company>
    implements CompanyService{
    @Resource
    private CompanyMapper companyMapper;
    @Override
    public Company getCompanyById(Long id) {
        ThrowUtil.throwIfTure(id <= 0, ErrorEnum.PARAMS_ERROR);
        return companyMapper.selectById(id);
    }

    /**
     * 所属公司员工数不为0时，也可以删除（注销）
     * @param id
     * @return
     */
    @Override
    public int deleteCompanyById(Long id) {
        ThrowUtil.throwIfTure(id <= 0, ErrorEnum.PARAMS_ERROR);
        return companyMapper.deleteById(id);
    }
}




