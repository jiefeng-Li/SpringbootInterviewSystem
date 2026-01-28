package com.cuit.interviewsystem.controller;


import com.cuit.interviewsystem.annotation.AuthCheck;
import com.cuit.interviewsystem.common.Result;
import com.cuit.interviewsystem.exception.BusinessException;
import com.cuit.interviewsystem.exception.ErrorEnum;
import com.cuit.interviewsystem.model.entity.Company;
import com.cuit.interviewsystem.model.enums.UserRoleEnum;
import com.cuit.interviewsystem.service.CompanyService;
import com.cuit.interviewsystem.utils.JWTUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RestController
@Slf4j
@RequestMapping("/company")
public class CompanyController {
    @Resource
    private CompanyService companyService;
    @Resource
    private JWTUtil jwtUtil;

    @GetMapping("/{id}")
    public Result<?> getCompanyById(@PathVariable Long id) {
        Company c = companyService.getCompanyById(id);
        //TODO 封装vo
        return Result.success();
    }

    @GetMapping("/token")
    public Result<?> getCompanyByToken() {
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
            //TODO 封装vo
            return Result.success();
        } catch (NumberFormatException e) {
            throw new BusinessException(ErrorEnum.PARAMS_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    @AuthCheck(roles = {UserRoleEnum.COMP_ADMIN, UserRoleEnum.SYS_ADMIN})
    //TODO 必须为对应公司管理员的用户删除/注销该公司
    public Result<?> deleteCompanyById(@PathVariable Long id) {
        companyService.deleteCompanyById(id);
        return Result.success();
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
}
