package com.cuit.interviewsystem.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cuit.interviewsystem.model.entity.OfferRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cuit.interviewsystem.model.dto.offer.OfferRecruiterPageDto;
import com.cuit.interviewsystem.model.dto.offer.OfferUserPageDto;
import com.cuit.interviewsystem.model.vo.OfferRecordVo;
import org.apache.ibatis.annotations.Mapper;

/**
* @author jiefe
* @description 针对表【t_offer_record(Offer记录表)】的数据库操作Mapper
* @createDate 2026-03-27 16:57:56
* @Entity generator.domain.OfferRecord
*/
@Mapper
public interface OfferRecordMapper extends BaseMapper<OfferRecord> {
	IPage<OfferRecordVo> getOfferPageByUser(Page<OfferRecordVo> page, OfferUserPageDto dto);

	IPage<OfferRecordVo> getOfferPageByRecruiter(Page<OfferRecordVo> page, OfferRecruiterPageDto dto);

	OfferRecordVo selectOfferVoById(Long id);

	Integer countPendingOfferByApplicationId(Long jobApplicationId);

}




