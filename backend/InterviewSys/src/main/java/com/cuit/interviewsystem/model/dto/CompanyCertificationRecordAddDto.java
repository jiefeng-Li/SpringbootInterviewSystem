package com.cuit.interviewsystem.model.dto;


import lombok.Data;

@Data
public class CompanyCertificationRecordAddDto {
    /**
     * 公司ID
     */
    private Long companyId;

    /**
     * 企业管理员ID 对应用户表user_id
     */
    private Long adminId;

    /**
     * 联系人
     */
    private String contactName;

    /**
     * 联系人电话
     */
    private String contactPhone;

    /**
     * 联系人邮箱
     */
    private String contactEmail;

    /**
     * 联系人职位
     */
    private String contactPosition;
}
