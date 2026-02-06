package com.cuit.interviewsystem.model.vo;


import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.util.Date;

@Data
public class CompanyCertificationRecordVo {
    private Long id;
    private Long companyId;
    private String companyName;
    private String companyLogoUrl;
    private Long adminId;
    private String adminName;
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private String contactPosition;
    private Integer status;
    private String reviewNotes;
    private Long reviewedBy;
    private String reviewedByName;
    private Integer isDeleted;
    private Date reviewedTime;
    private Date createTime;
    private Date updateTime;
}
