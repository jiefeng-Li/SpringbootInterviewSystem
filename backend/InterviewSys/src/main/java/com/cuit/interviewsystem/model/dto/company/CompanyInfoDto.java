package com.cuit.interviewsystem.model.dto.company;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serial;
import java.io.Serializable;

/**
 * 公司信息添加、修改dto
 */
@Data
public class CompanyInfoDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -3350086490852821341L;

    @NotNull
    @Min(value = 1, message = "公司传递错误")
    private Long companyId;
    private Long adminId;
    @NotBlank
    @Length(max = 100, message = "公司名称不能超过100个字符")
    private String companyName;
    /**
     * Logo
     */
    private MultipartFile logo;
    private String website;
    @Length(max = 1024, message = "公司简介，数据过长")
    private String introduction;
    @Length(max = 100, message = "公司行业，数据过长")
    private String industry;
    @NotBlank
    @Length(max = 100, message = "公司规模，数据过长")
    private String scale;
    @Length(max = 100, message = "公司地址，数据过长")
    private String city;
    /**
     * 状态(0待审,1正常,2驳回,3禁用)
     */
    private Integer status;

    /**
     * 营业执照URL
     */
    private MultipartFile businessLicense;
}
