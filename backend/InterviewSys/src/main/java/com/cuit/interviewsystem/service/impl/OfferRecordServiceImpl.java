package com.cuit.interviewsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cuit.interviewsystem.exception.ErrorEnum;
import com.cuit.interviewsystem.mapper.JobApplicationMapper;
import com.cuit.interviewsystem.model.dto.offer.OfferAddDto;
import com.cuit.interviewsystem.model.dto.offer.OfferRecruiterPageDto;
import com.cuit.interviewsystem.model.dto.offer.OfferStatusUpdateDto;
import com.cuit.interviewsystem.model.dto.offer.OfferUserPageDto;
import com.cuit.interviewsystem.model.entity.JobApplication;
import com.cuit.interviewsystem.model.entity.OfferRecord;
import com.cuit.interviewsystem.model.enums.JobApplicationStatusEnum;
import com.cuit.interviewsystem.model.enums.OfferStatusEnum;
import com.cuit.interviewsystem.model.vo.OfferRecordVo;
import com.cuit.interviewsystem.service.OfferRecordService;
import com.cuit.interviewsystem.mapper.OfferRecordMapper;
import com.cuit.interviewsystem.utils.ThrowUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
* @author jiefe
* @description 针对表【t_offer_record(Offer记录表)】的数据库操作Service实现
* @createDate 2026-03-27 16:57:56
*/
@Service
public class OfferRecordServiceImpl extends ServiceImpl<OfferRecordMapper, OfferRecord>
    implements OfferRecordService{

    @Resource
    private OfferRecordMapper offerRecordMapper;

    @Resource
    private JobApplicationMapper jobApplicationMapper;

    @Override
    @Transactional
    public void sendOffer(OfferAddDto dto) {
        JobApplication jobApplication = jobApplicationMapper.selectById(dto.getJobApplicationId());
        ThrowUtil.throwIfTrue(jobApplication == null || jobApplication.getIsDeleted() == 1,
            ErrorEnum.PARAMS_ERROR, "投递记录不存在");

        ThrowUtil.throwIfTrue(!dto.getCompanyId().equals(jobApplication.getCompanyId()),
            ErrorEnum.PARAMS_ERROR, "公司ID与投递记录不匹配");
        ThrowUtil.throwIfTrue(!dto.getJobSeekerId().equals(jobApplication.getUserId()),
            ErrorEnum.PARAMS_ERROR, "求职者ID与投递记录不匹配");
        ThrowUtil.throwIfTrue(!dto.getJobPositionId().equals(jobApplication.getJobPositionId()),
            ErrorEnum.PARAMS_ERROR, "职位ID与投递记录不匹配");

        ThrowUtil.throwIfTrue(!JobApplicationStatusEnum.checkStatus(jobApplication.getStatus(), JobApplicationStatusEnum.OFFER_SENT.getStatus()),
            ErrorEnum.PARAMS_ERROR, "当前投递状态不支持发送Offer");

        Integer pendingCount = offerRecordMapper.countPendingOfferByApplicationId(dto.getJobApplicationId());
        ThrowUtil.throwIfTrue(pendingCount != null && pendingCount > 0,
            ErrorEnum.OPTION_ERROR, "该投递已有待确认Offer");

        OfferRecord offerRecord = new OfferRecord();
        BeanUtils.copyProperties(dto, offerRecord);
        offerRecord.setStatus(OfferStatusEnum.PENDING.getStatus());
        offerRecordMapper.insert(offerRecord);

        jobApplication.setStatus(JobApplicationStatusEnum.OFFER_SENT.getStatus());
        jobApplication.setUpdateTime(LocalDate.now());
        jobApplicationMapper.update(jobApplication,
            new LambdaUpdateWrapper<JobApplication>().eq(JobApplication::getId, jobApplication.getId()));
    }

    @Override
    public void updateOfferStatus(OfferStatusUpdateDto dto) {
        OfferRecord offerRecord = offerRecordMapper.selectById(dto.getId());
        ThrowUtil.throwIfTrue(offerRecord == null || offerRecord.getIsDeleted() == 1,
            ErrorEnum.PARAMS_ERROR, "Offer记录不存在");

        ThrowUtil.throwIfTrue(!OfferStatusEnum.ACCEPTED.getStatus().equals(dto.getStatus())
                && !OfferStatusEnum.REJECTED.getStatus().equals(dto.getStatus()),
            ErrorEnum.PARAMS_ERROR, "仅支持接受或拒绝Offer");

        if (offerRecord.getOfferDeadline() != null && offerRecord.getOfferDeadline().isBefore(LocalDate.now())) {
            offerRecord.setStatus(OfferStatusEnum.EXPIRED.getStatus());
            offerRecord.setUpdateTime(LocalDate.now());
            offerRecordMapper.updateById(offerRecord);
            ThrowUtil.throwIfTrue(true, ErrorEnum.OPTION_ERROR, "Offer已过期，无法操作");
        }

        ThrowUtil.throwIfTrue(!OfferStatusEnum.PENDING.getStatus().equals(offerRecord.getStatus()),
            ErrorEnum.OPTION_ERROR, "当前Offer状态不支持该操作");

        offerRecord.setStatus(dto.getStatus());
        offerRecord.setRejectReason(dto.getRejectReason());
        offerRecord.setRespondTime(LocalDate.now());
        offerRecord.setUpdateTime(LocalDate.now());
        offerRecordMapper.updateById(offerRecord);

        JobApplication jobApplication = jobApplicationMapper.selectById(offerRecord.getJobApplicationId());
        ThrowUtil.throwIfTrue(jobApplication == null || jobApplication.getIsDeleted() == 1,
            ErrorEnum.PARAMS_ERROR, "投递记录不存在");

        if (OfferStatusEnum.ACCEPTED.getStatus().equals(dto.getStatus())) {
            ThrowUtil.throwIfTrue(!JobApplicationStatusEnum.checkStatus(jobApplication.getStatus(), JobApplicationStatusEnum.HIRED.getStatus()),
                ErrorEnum.OPTION_ERROR, "当前投递状态不允许更新为已录用");
            jobApplication.setStatus(JobApplicationStatusEnum.HIRED.getStatus());
        } else {
            ThrowUtil.throwIfTrue(!JobApplicationStatusEnum.checkStatus(jobApplication.getStatus(), JobApplicationStatusEnum.ELIMINATED.getStatus()),
                ErrorEnum.OPTION_ERROR, "当前投递状态不允许更新为已淘汰");
            jobApplication.setStatus(JobApplicationStatusEnum.ELIMINATED.getStatus());
        }
        jobApplication.setUpdateTime(LocalDate.now());
        jobApplicationMapper.update(jobApplication,
            new LambdaUpdateWrapper<JobApplication>().eq(JobApplication::getId, jobApplication.getId()));
    }

    @Override
    public Page<OfferRecordVo> getOfferListByUser(OfferUserPageDto dto) {
        Page<OfferRecordVo> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        offerRecordMapper.getOfferPageByUser(page, dto);
        return page;
    }

    @Override
    public Page<OfferRecordVo> getOfferListByRecruiter(OfferRecruiterPageDto dto) {
        Page<OfferRecordVo> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        offerRecordMapper.getOfferPageByRecruiter(page, dto);
        return page;
    }

    @Override
    public OfferRecordVo getOfferById(Long id) {
        ThrowUtil.throwIfTrue(id == null, ErrorEnum.PARAMS_ERROR, "Offer记录ID不能为空");
        OfferRecordVo offer = offerRecordMapper.selectOfferVoById(id);
        ThrowUtil.throwIfTrue(offer == null, ErrorEnum.NOT_FOUND_ERROR, "Offer记录不存在");
        return offer;
    }

}




