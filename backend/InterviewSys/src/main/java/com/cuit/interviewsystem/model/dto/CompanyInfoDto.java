package com.cuit.interviewsystem.model.dto;


import lombok.Data;

import java.io.Serializable;

/**
 * 公司信息添加、修改dto
 */
@Data
public class CompanyInfoDto implements Serializable {
    private static final long serialVersionUID = -3350086490852821341L;

    private Long companyId;
    private Long adminId;
    private String companyName;
    /**
     * Logo URL
     */
    private String logoUrl;
    private String website;
    private String introduction;
    private String industry;
    private String scale;
    private String city;
    /**
     * 状态(0待审,1正常,2驳回,3禁用)
     */
    private Integer status;

    /**
     * 营业执照URL
     */
    private String businessLicenseUrl;
    private Integer isDeleted;
}
