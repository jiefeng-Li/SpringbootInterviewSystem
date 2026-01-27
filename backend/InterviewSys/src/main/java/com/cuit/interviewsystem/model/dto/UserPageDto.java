package com.cuit.interviewsystem.model.dto;

import lombok.Data;

@Data
public class UserPageDto {
    //必须
    private Long pageSize;
    //必须
    private Long pageNum;
    private String username;
    private String role;
    private String email;
    private String phone;
    private Integer accountStatus;
    //必须
    private Integer isDeleted;
    private Long companyId;
}
