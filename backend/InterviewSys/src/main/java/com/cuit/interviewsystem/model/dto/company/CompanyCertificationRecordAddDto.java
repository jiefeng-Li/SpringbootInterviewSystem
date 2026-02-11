package com.cuit.interviewsystem.model.dto.company;


import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class CompanyCertificationRecordAddDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 6886969555309567646L;
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
