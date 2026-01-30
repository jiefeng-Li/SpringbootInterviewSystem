package com.cuit.interviewsystem.model.dto;


import lombok.Data;

@Data
public class CompanyUserPageDto {
    //必须
    private Long pageSize;
    //必须
    private Long pageNum;
    private Long companyId;
    private String role;
}
