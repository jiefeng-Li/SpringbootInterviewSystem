package com.cuit.interviewsystem.model.dto;


import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CompanyUserPageDto extends PageDto{
    private Long companyId;
    private String role;
}
