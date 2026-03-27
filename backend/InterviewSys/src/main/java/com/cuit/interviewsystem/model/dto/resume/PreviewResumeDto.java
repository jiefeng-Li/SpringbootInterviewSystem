package com.cuit.interviewsystem.model.dto.resume;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * 预览简历DTO
 * 用于预览未保存的简历
 */
@Data
public class PreviewResumeDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 性别(0男,1女,2保密)
     */
    private Integer gender;

    /**
     * 出生日期
     */
    private LocalDate birthday;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 现居地址
     */
    private String address;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 期望工作城市
     */
    private String city;

    /**
     * 个人简介/求职意向
     */
    private String summary;

    /**
     * 教育经历
     */
    private List<AddResumeEducationDto> educations;

    /**
     * 工作经历
     */
    private List<AddResumeExperiencesDto> experiences;

    /**
     * 项目经历
     */
    private List<AddResumeProjectDto> projects;
}
