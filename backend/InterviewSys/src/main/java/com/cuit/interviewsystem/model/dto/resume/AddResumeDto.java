package com.cuit.interviewsystem.model.dto.resume;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
public class AddResumeDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -5438122127871171490L;
    /**
     * 用户ID
     */
    @NotNull(message = "简历用户ID不能为空")
    private Long userId;

    /**
     * 简历模板ID
     */
    @NotNull(message = "简历模板ID不能为空")
    private Integer templateId;

    /**
     * 姓名
     */
    @NotBlank(message = "姓名不能为空")
    @Length(max = 64, message = "姓名过长")
    private String name;

    /**
     * 性别(0男,1女,2保密)
     */
    @NotNull(message = "性别不能为空")
    private Integer gender;

    /**
     * 出生日期
     */
    private LocalDate birthday;

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    private String phone;

    /**
     * 邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    private String email;

    /**
     * 现居地址
     */
    @Length(max = 64, message = "现居地址过长")
    private String address;

    /**
     * 期望工作城市
     */
    private String city;

    /**
     * 个人简介/求职意向
     */
    @Length(max = 500, message = "个人简介/求职意向不能超过500字")
    private String summary;
    private Integer isDefault = 0;


    private List<AddResumeEducationDto> educations;

    private List<AddResumeExperiencesDto> experiences;

    private List<AddResumeProjectDto> projects;
}
