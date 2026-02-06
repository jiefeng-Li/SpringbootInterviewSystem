package com.cuit.interviewsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cuit.interviewsystem.model.entity.Company;
import org.apache.ibatis.annotations.Mapper;

/**
* @author jiefe
* @description 针对表【t_company(公司基本信息表)】的数据库操作Mapper
* @createDate 2026-01-28 20:13:34
* @Entity com.cuit.interviewsystem.model.entity.Company
*/
@Mapper
public interface CompanyMapper extends BaseMapper<Company> {

}




