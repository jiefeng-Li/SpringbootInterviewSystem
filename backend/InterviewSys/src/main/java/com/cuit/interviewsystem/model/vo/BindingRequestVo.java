package com.cuit.interviewsystem.model.vo;

import lombok.Data;

import java.util.Date;

@Data
public class BindingRequestVo {
    private Long id;
    private Long userId;
    private String username;
    private String userPhone;
    private String companyName;
    private String applicationNotes;
    private Long status;
    private Long reviewedBy;
    private String reviewedByName;
    private String reviewNotes;
    private Date reviewedTime;
    private Date expiresAt;
    private Integer isDeleted;
    private Date createTime;
    private Date updateTime;
}
