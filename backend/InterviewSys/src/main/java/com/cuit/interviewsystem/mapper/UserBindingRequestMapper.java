package com.cuit.interviewsystem.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cuit.interviewsystem.model.dto.BindingRequestPageDto;
import com.cuit.interviewsystem.model.vo.BindingRequestVo;
import com.cuit.interviewsystem.model.entity.BindingRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author jiefe
* @description 针对表【t_binding_request(招聘者绑定公司申请记录表)】的数据库操作Mapper
* @createDate 2026-02-07 15:47:56
* @Entity generator.domain.BindingRequest
*/
@Mapper
public interface UserBindingRequestMapper extends BaseMapper<BindingRequest> {
    IPage<BindingRequestVo> getBindingPage(IPage<?> page, BindingRequestPageDto dto);
}