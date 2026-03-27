package com.cuit.interviewsystem.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cuit.interviewsystem.annotation.AuthCheck;
import com.cuit.interviewsystem.common.Result;
import com.cuit.interviewsystem.model.dto.offer.OfferAddDto;
import com.cuit.interviewsystem.model.dto.offer.OfferRecruiterPageDto;
import com.cuit.interviewsystem.model.dto.offer.OfferStatusUpdateDto;
import com.cuit.interviewsystem.model.dto.offer.OfferUserPageDto;
import com.cuit.interviewsystem.model.enums.UserRoleEnum;
import com.cuit.interviewsystem.model.vo.OfferRecordVo;
import com.cuit.interviewsystem.model.vo.PageVo;
import com.cuit.interviewsystem.service.OfferRecordService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/offerRecord")
@Tag(name = "offer")
public class OfferRecordController {

	@Resource
	private OfferRecordService offerRecordService;

	@PostMapping("/send")
	@AuthCheck(roles = {UserRoleEnum.RECRUITER})
	public Result<Void> sendOffer(@Valid @RequestBody OfferAddDto dto) {
		offerRecordService.sendOffer(dto);
		return Result.success();
	}

	@PostMapping("/status")
	@AuthCheck(roles = {UserRoleEnum.JOB_SEEKER})
	public Result<Void> updateOfferStatus(@Valid @RequestBody OfferStatusUpdateDto dto) {
		offerRecordService.updateOfferStatus(dto);
		return Result.success();
	}

	@GetMapping("/list/by-user")
	@AuthCheck(roles = {UserRoleEnum.JOB_SEEKER, UserRoleEnum.RECRUITER})
	public Result<PageVo<OfferRecordVo>> getOfferListByUser(@Valid OfferUserPageDto dto) {
		Page<OfferRecordVo> page = offerRecordService.getOfferListByUser(dto);
		return Result.success(PageVo.of(page));
	}

	@GetMapping("/list/sent")
	@AuthCheck(roles = {UserRoleEnum.RECRUITER})
	public Result<PageVo<OfferRecordVo>> getOfferListByRecruiter(@Valid OfferRecruiterPageDto dto) {
		Page<OfferRecordVo> page = offerRecordService.getOfferListByRecruiter(dto);
		return Result.success(PageVo.of(page));
	}

	@GetMapping
	@AuthCheck(roles = {UserRoleEnum.JOB_SEEKER, UserRoleEnum.RECRUITER})
	public Result<OfferRecordVo> getOfferById(Long id) {
		return Result.success(offerRecordService.getOfferById(id));
	}
}
