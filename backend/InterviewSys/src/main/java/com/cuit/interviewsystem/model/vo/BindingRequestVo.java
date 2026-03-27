package com.cuit.interviewsystem.model.vo;

import lombok.Data;

import java.time.LocalDate;


@Data
public class BindingRequestVo {
    private Long id;
    private Long userId;
    private String username;
    private String userPhone;
    private String companyName;
    private String applicationNotes;
    private Integer status;
    private Long reviewedBy;
    private String reviewedByName;
    private String reviewNotes;
    private LocalDate reviewedTime;
    private LocalDate expiresAt;
    private Integer isDeleted;
    private LocalDate createTime;
    private LocalDate updateTime;
}
