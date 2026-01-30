package com.cuit.interviewsystem.model.vo;


import lombok.Data;

import java.util.Date;

@Data
public class CompanyVo {
    private Long companyId;
    private Long adminId;

    /**
     * 公司全称
     */
    private String companyName;

    /**
     * Logo URL
     */
    private String logoUrl;

    /**
     * 官网
     */
    private String website;

    /**
     * 公司简介
     */
    private String introduction;

    /**
     * 所属行业
     */
    private String industry;

    /**
     * 公司规模
     */
    private String scale;

    /**
     * 所在城市
     */
    private String city;

    /**
     * 状态(0待审,1正常,2驳回,3禁用)
     * String 不同于entity
     * CompanyStatusEnum 的 text
     */
    private String status;

    /**
     * 营业执照URL
     */
    private String businessLicenseUrl;
    private Date createTime;
}
