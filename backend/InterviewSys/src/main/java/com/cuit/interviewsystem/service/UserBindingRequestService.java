package com.cuit.interviewsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cuit.interviewsystem.model.dto.BindingRequestDto;
import com.cuit.interviewsystem.model.dto.BindingRequestPageDto;
import com.cuit.interviewsystem.model.entity.BindingRequest;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cuit.interviewsystem.model.vo.BindingRequestVo;

/**
* @author jiefe
* @description 针对表【t_binding_request(招聘者绑定公司申请记录表)】的数据库操作Service
* @createDate 2026-02-07 15:47:56
*/
public interface UserBindingRequestService extends IService<BindingRequest> {

    String bindingCompany(BindingRequestDto bindingRequestDto);

    String unbindCompany(Long id);

    Page<BindingRequestVo> getBindingList(BindingRequestPageDto pageDto);

    BindingRequest getRequestById(Long id);
}
