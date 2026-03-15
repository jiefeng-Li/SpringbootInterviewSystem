package com.cuit.interviewsystem.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cuit.interviewsystem.annotation.AuthCheck;
import com.cuit.interviewsystem.common.Result;
import com.cuit.interviewsystem.exception.BusinessException;
import com.cuit.interviewsystem.exception.ErrorEnum;
import com.cuit.interviewsystem.model.dto.company.CompanyAddDto;
import com.cuit.interviewsystem.model.dto.company.CompanyInfoDto;
import com.cuit.interviewsystem.model.dto.company.CompanySearchPageDto;
import com.cuit.interviewsystem.model.enums.CompanyStatusEnum;
import com.cuit.interviewsystem.model.enums.UserRoleEnum;
import com.cuit.interviewsystem.model.vo.CompanyVo;
import com.cuit.interviewsystem.model.vo.PageVo;
import com.cuit.interviewsystem.service.CompanyService;
import com.cuit.interviewsystem.utils.JWTUtil;
import com.cuit.interviewsystem.utils.ThrowUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/company")
@Tag(name = "公司接口", description = "公司接口")
public class CompanyController {
    @Resource
    private CompanyService companyService;
    @Resource
    private JWTUtil jwtUtil;

    @GetMapping("/{id}")
    public Result<CompanyVo> getCompanyById(@PathVariable Long id) {
        CompanyVo c = companyService.getCompanyVoById(id);
        return Result.success(c);
    }

    @GetMapping("/token")
    @AuthCheck
    public Result<CompanyVo> getCompanyByToken() {
        String cmpId = jwtUtil.getLoginUserInfo(JWTUtil.ELEMENT.COMPANY_ID);
        ThrowUtil.throwIfTrue(cmpId == null, ErrorEnum.PARAMS_ERROR, "当前用户未绑定公司");
        return Result.success(companyService.getCompanyVoById(Long.parseLong(cmpId)));
    }

    @GetMapping("/status")
    public Result<?> getCompanyStatusEnum() {
        record StatusRecord(Integer status, String text){}
        List<StatusRecord> res = new ArrayList<>();
        for (CompanyStatusEnum c : CompanyStatusEnum.values())
            res.add(new StatusRecord(c.getStatus(), c.getText()));
        return Result.success(res);
    }

    @GetMapping("/list")
    @AuthCheck
    public Result<PageVo<CompanyVo>> getCompanyList(CompanySearchPageDto dto) {
        Page<CompanyVo> res = companyService.getCompanyList(dto);
        return Result.success(PageVo.of(res));
    }

    @DeleteMapping("/{id}")
    @AuthCheck(roles = {UserRoleEnum.SYS_ADMIN})
    public Result<?> deleteCompanyById(@PathVariable Long id) {
        int i = companyService.deleteCompanyById(id);
        return Result.success(null, i <= 0 ? "删除公司不存在或已被删除" : "删除成功");
    }

    @PutMapping("/deregister/{id}")
    @AuthCheck(roles = {UserRoleEnum.COMP_ADMIN})
    public Result<?> deregisterCompanyById(@PathVariable Long id) {
        int i = companyService.deregisterCompanyById(id);
        if (i <= 0)
            return Result.error(ErrorEnum.SYSTEM_ERROR, "注销失败");
        return Result.success(null, "注销成功");
    }

    @PutMapping("/{id}")
    public Result<Long> updateCompanyById(@PathVariable Long id, @RequestBody CompanyInfoDto cid) {
        int i = companyService.updateCompanyById(id, cid);
        if (i <= 0)
            throw new BusinessException(ErrorEnum.NOT_FOUND_ERROR);
        return Result.success(null, "更新成功");
    }

    /**
     * HttpMediaTypeNotSupportedException 表明Spring无法处理 multipart/form-data 类型的请求。
     * 使用了 @RequestBody 注解：这个注解用于接收JSON/XML等格式，不能用于接收文件上传
     * @param cad
     * @return 放入公司id的token
     */
    @PostMapping
    @AuthCheck(roles = {UserRoleEnum.COMP_ADMIN})
    public Result<String> addOneCompany(@ModelAttribute CompanyAddDto cad) {
        String newToken = companyService.addOneCompany(cad);
        return Result.success(newToken,"添加成功");
    }
}
