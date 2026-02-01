package com.cuit.interviewsystem.service.impl;

import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cuit.interviewsystem.exception.BusinessException;
import com.cuit.interviewsystem.exception.ErrorEnum;
import com.cuit.interviewsystem.mapper.CompanyMapper;
import com.cuit.interviewsystem.mapper.UserMapper;
import com.cuit.interviewsystem.model.dto.CompanyCertification4AdminDto;
import com.cuit.interviewsystem.model.dto.CompanyCertificationRecordAddDto;
import com.cuit.interviewsystem.model.entity.Company;
import com.cuit.interviewsystem.model.entity.CompanyCertificationRecord;
import com.cuit.interviewsystem.model.enums.CompanyCertificationStatusEnum;
import com.cuit.interviewsystem.model.enums.CompanyStatusEnum;
import com.cuit.interviewsystem.model.enums.UserRoleEnum;
import com.cuit.interviewsystem.service.CompanyCertificationRecordService;
import com.cuit.interviewsystem.mapper.CompanyCertificationRecordMapper;
import com.cuit.interviewsystem.utils.JWTUtil;
import com.cuit.interviewsystem.utils.ThrowUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;
import java.util.Objects;

/**
* @author jiefe
* @description 针对表【t_company_certification_record(公司资质认证记录表)】的数据库操作Service实现
* @createDate 2026-02-01 16:25:01
*/
@Service
@Slf4j
public class CompanyCertificationRecordServiceImpl extends ServiceImpl<CompanyCertificationRecordMapper, CompanyCertificationRecord>
    implements CompanyCertificationRecordService{
    @Resource
    private CompanyCertificationRecordMapper recordMapper;
    @Resource
    private JWTUtil jwtUtil;
    @Resource
    private UserMapper userMapper;
    @Resource
    private CompanyMapper companyMapper;

    @Value("${const-var.email-match-rule}")
    private String EMAIL_MATCH_RULE;


    /**
     * 企业管理员进行认证企业
     * @param ccrad
     * @return
     */
    @Override
    public long addCompanyCertificationRecord(CompanyCertificationRecordAddDto ccrad) {
        CompanyCertificationRecord newRecord = new CompanyCertificationRecord();
        BeanUtils.copyProperties(ccrad, newRecord);
        objectCheck(newRecord);
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        String token = request.getHeader("token");
        UserRoleEnum ure = UserRoleEnum.getRole(jwtUtil.parse(token, JWTUtil.ELEMENT_ROLE));
        ThrowUtil.throwIfTure(!UserRoleEnum.COMP_ADMIN.equals(ure), ErrorEnum.UNAUTHORIZED);
        newRecord.setAdminId(ccrad.getAdminId());
        return recordMapper.insert(newRecord);
    }

    /**
     * 管理员提交的公司认证意见
     * @param dto
     * @return
     */
    @Override
    @Transactional
    public int reviewCompanyCertification(CompanyCertification4AdminDto dto) {
        ThrowUtil.throwIfTure(dto.getId() == null, ErrorEnum.PARAMS_ERROR);
        CompanyCertificationRecord record = recordMapper.selectById(dto.getId());
        ThrowUtil.throwIfTure(record == null, ErrorEnum.PARAMS_ERROR.getCode(), "认证记录不存在");
        ThrowUtil.throwIfTure(record.getIsDeleted() == 1, ErrorEnum.PARAMS_ERROR.getCode(), "认证记录已删除");
        ThrowUtil.throwIfTure(!Objects.equals(record.getStatus(), CompanyCertificationStatusEnum.REVIEWING.getStatus()),
                ErrorEnum.PARAMS_ERROR.getCode(), "认证记录已审核");
        //工具认证记录的审核情况，修改公司状态
        Company company = companyMapper.selectById(record.getCompanyId());
        CompanyCertificationStatusEnum certificationStatus = CompanyCertificationStatusEnum.getEnum(dto.getStatus());
        ThrowUtil.throwIfTure(certificationStatus == null, ErrorEnum.PARAMS_ERROR.getCode(), "认证状态异常");
        switch (certificationStatus) {
            case CompanyCertificationStatusEnum.PASS:
                company.setStatus(CompanyStatusEnum.NORMAL.getStatus());
                break;
            case CompanyCertificationStatusEnum.REFUSE:
                company.setStatus(CompanyStatusEnum.REFUSE.getStatus());
                break;
            default:
                throw new BusinessException(ErrorEnum.PARAMS_ERROR.getCode(), "认证状态异常");
        }
        company.setEditTime(new Date());
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        String token = request.getHeader("token");
        Long reviewBy = Long.valueOf(jwtUtil.parse(token, JWTUtil.ELEMENT_USER_ID));
        record.setReviewedBy(reviewBy);
        record.setReviewedTime(new Date());
        record.setUpdateTime(new Date());
        record.setReviewNotes(dto.getReviewNotes());
        record.setStatus(dto.getStatus());
        //更新数据--事务
        recordMapper.updateById(record);
        return companyMapper.updateById(company);
    }

    private void objectCheck(CompanyCertificationRecord companyCertificationRecord) {
        Company cmp = companyMapper.selectById(companyCertificationRecord.getCompanyId());
        ThrowUtil.throwIfTure(cmp == null, ErrorEnum.PARAMS_ERROR.getCode(), "公司不存在");
        //公司状态
        CompanyStatusEnum cse = CompanyStatusEnum.getEnum(cmp.getStatus());
        ThrowUtil.throwIfTure(CompanyStatusEnum.NORMAL.equals(cse), ErrorEnum.PARAMS_ERROR.getCode(), "公司状态异常");
        ThrowUtil.throwIfTure(StrUtil.isBlank(companyCertificationRecord.getContactName()),
                ErrorEnum.PARAMS_ERROR.getCode(), "联系人不能为空");
        ThrowUtil.throwIfTure(companyCertificationRecord.getContactName().length() >= 50,
                ErrorEnum.PARAMS_ERROR.getCode(), "联系人长度不能超过50");
        ThrowUtil.throwIfTure(StrUtil.isBlank(companyCertificationRecord.getContactPhone()),
                ErrorEnum.PARAMS_ERROR.getCode(), "联系人电话不能为空");
        ThrowUtil.throwIfTure(PhoneUtil.isPhone(companyCertificationRecord.getContactPhone()),
                ErrorEnum.PARAMS_ERROR.getCode(), "联系人电话格式不正确");
        ThrowUtil.throwIfTure(StrUtil.isBlank(companyCertificationRecord.getContactEmail()),
                ErrorEnum.PARAMS_ERROR.getCode(), "联系人邮箱不能为空");
        ThrowUtil.throwIfTure(companyCertificationRecord.getContactEmail().matches(EMAIL_MATCH_RULE),
                ErrorEnum.PARAMS_ERROR.getCode(), "联系人邮箱格式不正确");
    }
}




