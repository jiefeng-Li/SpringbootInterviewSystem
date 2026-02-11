package com.cuit.interviewsystem.service.impl;

import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cuit.interviewsystem.exception.BusinessException;
import com.cuit.interviewsystem.exception.ErrorEnum;
import com.cuit.interviewsystem.mapper.CompanyMapper;
import com.cuit.interviewsystem.mapper.UserMapper;
import com.cuit.interviewsystem.model.dto.user.AdminReviewCertificationDto;
import com.cuit.interviewsystem.model.dto.company.CertificationRecordPageDto;
import com.cuit.interviewsystem.model.dto.company.CompanyCertificationRecordAddDto;
import com.cuit.interviewsystem.model.entity.Company;
import com.cuit.interviewsystem.model.entity.CompanyCertificationRecord;
import com.cuit.interviewsystem.model.entity.User;
import com.cuit.interviewsystem.model.enums.CompanyCertificationStatusEnum;
import com.cuit.interviewsystem.model.enums.CompanyStatusEnum;
import com.cuit.interviewsystem.model.vo.CompanyCertificationRecordVo;
import com.cuit.interviewsystem.service.CompanyCertificationRecordService;
import com.cuit.interviewsystem.mapper.CompanyCertificationRecordMapper;
import com.cuit.interviewsystem.utils.JWTUtil;
import com.cuit.interviewsystem.utils.ThrowUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * 提交当前登录的企业管理员，以及其所属的公司的认证记录
     * @param ccrad
     * @return
     */
    @Override
    public long addCompanyCertificationRecord(CompanyCertificationRecordAddDto ccrad) {
        User curUser = jwtUtil.parseLoginUser();
        ThrowUtil.throwIfTrue(curUser.getCompanyId() == null, ErrorEnum.NOT_FOUND_ERROR, "请先注册企业");
        ThrowUtil.throwIfTrue(recordMapper.exists(new LambdaQueryWrapper<CompanyCertificationRecord>()
                        .eq(CompanyCertificationRecord::getCompanyId, curUser.getCompanyId())
                        .eq(CompanyCertificationRecord::getStatus,
                                CompanyCertificationStatusEnum.REVIEWING.getStatus())
                        .eq(CompanyCertificationRecord::getIsDeleted, 0)),
                ErrorEnum.PARAMS_ERROR, "有未被审核的认证记录");
        CompanyCertificationRecord newRecord = new CompanyCertificationRecord();
        BeanUtils.copyProperties(ccrad, newRecord);
        newRecord.setAdminId(curUser.getUserId());
        newRecord.setCompanyId(curUser.getCompanyId());
        objectCheck(newRecord);
        return recordMapper.insert(newRecord) == 0 ? 0 : newRecord.getId();
    }

    /**
     * 管理员提交的公司认证意见
     * @param dto
     * @return
     */
    @Override
    @Transactional
    public int reviewCompanyCertification(AdminReviewCertificationDto dto) {
        ThrowUtil.throwIfTrue(dto.getId() == null, ErrorEnum.PARAMS_ERROR);
        CompanyCertificationRecord record = recordMapper.selectById(dto.getId());
        ThrowUtil.throwIfTrue(record == null, ErrorEnum.PARAMS_ERROR, "认证记录不存在");
        ThrowUtil.throwIfTrue(record.getIsDeleted() == 1, ErrorEnum.PARAMS_ERROR, "认证记录已删除");
        ThrowUtil.throwIfTrue(!Objects.equals(record.getStatus(), CompanyCertificationStatusEnum.REVIEWING.getStatus()),
                ErrorEnum.PARAMS_ERROR, "认证记录已审核");
        //工具认证记录的审核情况，修改公司状态
        Company company = companyMapper.selectById(record.getCompanyId());
        CompanyCertificationStatusEnum certificationStatus = CompanyCertificationStatusEnum.getEnum(dto.getStatus());
        switch (certificationStatus) {
            case CompanyCertificationStatusEnum.PASS:
                company.setStatus(CompanyStatusEnum.NORMAL.getStatus());
                break;
            case CompanyCertificationStatusEnum.REFUSE:
                company.setStatus(CompanyStatusEnum.REVIEWING.getStatus());
                break;
            default:
                throw new BusinessException(ErrorEnum.PARAMS_ERROR.getCode(), "认证状态异常");
        }
        company.setEditTime(new Date());
        long reviewBy = Long.parseLong(jwtUtil.getLoginUserInfo(JWTUtil.ELEMENT.USER_ID));
        record.setReviewedBy(reviewBy);
        record.setReviewedTime(new Date());
        record.setUpdateTime(new Date());
        record.setReviewNotes(dto.getReviewNotes());
        record.setStatus(dto.getStatus());
        //更新数据--事务
        recordMapper.updateById(record);
        return companyMapper.updateById(company);
    }

    @Override
    public Page<CompanyCertificationRecordVo> getRecords(CertificationRecordPageDto dto) {
        Page<CompanyCertificationRecordVo> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        recordMapper.getRecord(page, dto);
        return page;
    }

    @Override
    public int deleteRecordById(Long id) {
        ThrowUtil.throwIfTrue(id == null || id <= 0, ErrorEnum.PARAMS_ERROR, "认证记录id不能为空");
        return recordMapper.deleteById(id);
    }

    private void objectCheck(CompanyCertificationRecord companyCertificationRecord) {
        Company cmp = companyMapper.selectById(companyCertificationRecord.getCompanyId());
        ThrowUtil.throwIfTrue(cmp == null, ErrorEnum.PARAMS_ERROR, "公司不存在");
        //公司状态
        CompanyStatusEnum cse = CompanyStatusEnum.getEnum(cmp.getStatus());
        ThrowUtil.throwIfTrue(!CompanyStatusEnum.REVIEWING.equals(cse),
                ErrorEnum.PARAMS_ERROR, "公司已通过审核或被冻结");
        ThrowUtil.throwIfTrue(StrUtil.isBlank(companyCertificationRecord.getContactName()),
                ErrorEnum.PARAMS_ERROR, "联系人不能为空");
        ThrowUtil.throwIfTrue(companyCertificationRecord.getContactName().length() >= 50,
                ErrorEnum.PARAMS_ERROR, "联系人长度不能超过50");
        ThrowUtil.throwIfTrue(StrUtil.isBlank(companyCertificationRecord.getContactPhone()),
                ErrorEnum.PARAMS_ERROR, "联系人电话不能为空");
        ThrowUtil.throwIfTrue(!PhoneUtil.isPhone(companyCertificationRecord.getContactPhone()),
                ErrorEnum.PARAMS_ERROR, "联系人电话格式不正确");
        ThrowUtil.throwIfTrue(StrUtil.isBlank(companyCertificationRecord.getContactEmail()),
                ErrorEnum.PARAMS_ERROR, "联系人邮箱不能为空");
        ThrowUtil.throwIfTrue(!companyCertificationRecord.getContactEmail().matches(EMAIL_MATCH_RULE),
                ErrorEnum.PARAMS_ERROR, "联系人邮箱格式不正确");
    }
}




