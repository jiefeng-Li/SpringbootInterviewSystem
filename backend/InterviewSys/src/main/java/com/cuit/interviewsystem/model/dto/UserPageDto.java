package com.cuit.interviewsystem.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserPageDto extends PageDto{
    private String username;
    private String role;
    private String email;
    private String phone;
    private Integer accountStatus;
    //必须
    private Integer isDeleted;
    private Long companyId;
}
