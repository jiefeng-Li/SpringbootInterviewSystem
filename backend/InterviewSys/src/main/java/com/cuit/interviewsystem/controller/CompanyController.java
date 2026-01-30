package com.cuit.interviewsystem.controller;


import com.cuit.interviewsystem.annotation.AuthCheck;
import com.cuit.interviewsystem.common.Result;
import com.cuit.interviewsystem.exception.BusinessException;
import com.cuit.interviewsystem.exception.ErrorEnum;
import com.cuit.interviewsystem.model.dto.CompanyUserPageDto;
import com.cuit.interviewsystem.model.entity.Company;
import com.cuit.interviewsystem.model.enums.CompanyStatusEnum;
import com.cuit.interviewsystem.model.enums.UserRoleEnum;
import com.cuit.interviewsystem.model.vo.CompanyVo;
import com.cuit.interviewsystem.model.vo.PageVo;
import com.cuit.interviewsystem.model.vo.UserVo;
import com.cuit.interviewsystem.service.CompanyService;
import com.cuit.interviewsystem.utils.JWTUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/company")
public class CompanyController {
    @Resource
    private CompanyService companyService;
    @Resource
    private JWTUtil jwtUtil;

    @GetMapping("/{id}")
    public Result<CompanyVo> getCompanyById(@PathVariable Long id) {
        Company c = companyService.getCompanyById(id);
        CompanyVo res = new CompanyVo();
        BeanUtils.copyProperties(c, res);
        CompanyStatusEnum cse = CompanyStatusEnum.getEnum(c.getStatus());
        if (cse != null)
            res.setStatus(cse.getText());
        return Result.success(res);
    }

    @GetMapping("/token")
    public Result<CompanyVo> getCompanyByToken() {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        String token = request.getHeader("token");
        String parse = jwtUtil.parse(token, JWTUtil.ELEMENT_COMPANY_ID);
        try {
            Company company = null;
            if (parse != null) {
                long l = Long.parseLong(parse);
                company = companyService.getCompanyById(l);
            }
            CompanyVo res = new CompanyVo();
            BeanUtils.copyProperties(company, res);
            CompanyStatusEnum cse = CompanyStatusEnum.getEnum(company.getStatus());
            if (cse != null)
                res.setStatus(cse.getText());
            return Result.success(res);
        } catch (NumberFormatException e) {
            throw new BusinessException(ErrorEnum.PARAMS_ERROR);
        }
    }

    @GetMapping("/status")
    public Result<?> getCompanyStatusEnum() {
        record StatusRecord(Integer status, String text){}
        List<StatusRecord> res = new ArrayList<>();
        for (CompanyStatusEnum c : CompanyStatusEnum.values())
            res.add(new StatusRecord(c.getStatus(), c.getText()));
        return Result.success(res);
    }

    @DeleteMapping("/{id}")
    @AuthCheck(roles = {UserRoleEnum.COMP_ADMIN, UserRoleEnum.SYS_ADMIN})
    //TODO 必须为对应公司管理员的用户删除/注销该公司
    public Result<?> deleteCompanyById(@PathVariable Long id) {
        int i = companyService.deleteCompanyById(id);
        return Result.success(null, i <= 0 ? "删除公司不存在或已被删除" : "删除成功");
    }

    @PutMapping("/{id}")
    public Result<Long> updateCompanyById(@PathVariable Long id) {
        //TODO 封装dto
        return Result.success();
    }

    @PostMapping
    public Result<Long> addOneCompany(@RequestBody Company company) {
        //TODO 封装dto
        return Result.success();
    }

    /**
     * 获取公司用户列表
     * @param dto
     * @return
     */
    @GetMapping("/user/list")
    @AuthCheck(roles = {UserRoleEnum.COMP_ADMIN, UserRoleEnum.SYS_ADMIN})
    public Result<PageVo<UserVo>> getCompanyUsers(CompanyUserPageDto dto) {
        return Result.success();
    }
}
