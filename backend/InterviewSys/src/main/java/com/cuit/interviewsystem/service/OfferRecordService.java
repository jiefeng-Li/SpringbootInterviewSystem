package com.cuit.interviewsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cuit.interviewsystem.model.dto.offer.OfferAddDto;
import com.cuit.interviewsystem.model.dto.offer.OfferRecruiterPageDto;
import com.cuit.interviewsystem.model.dto.offer.OfferStatusUpdateDto;
import com.cuit.interviewsystem.model.dto.offer.OfferUserPageDto;
import com.cuit.interviewsystem.model.entity.OfferRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cuit.interviewsystem.model.vo.OfferRecordVo;

/**
* @author jiefe
* @description 针对表【t_offer_record(Offer记录表)】的数据库操作Service
* @createDate 2026-03-27 16:57:56
*/
public interface OfferRecordService extends IService<OfferRecord> {
	void sendOffer(OfferAddDto dto);

	void updateOfferStatus(OfferStatusUpdateDto dto);

	Page<OfferRecordVo> getOfferListByUser(OfferUserPageDto dto);

	Page<OfferRecordVo> getOfferListByRecruiter(OfferRecruiterPageDto dto);

	OfferRecordVo getOfferById(Long id);

}
