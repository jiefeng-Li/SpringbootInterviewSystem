package com.cuit.interviewsystem.model.dto;


import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CertificationRecordPageDto extends PageDto{
    private Long companyId;
    private Long reviewBy;
    private Integer status;
    //默认为0
    private Integer isDeleted = 0;
}
