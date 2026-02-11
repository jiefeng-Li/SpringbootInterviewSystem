package com.cuit.interviewsystem.model.dto.user;

import com.cuit.interviewsystem.model.dto.PageDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserPageDto extends PageDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -149938697090689509L;
    private String username;
    private String role;
    private String email;
    private String phone;
    private Integer accountStatus;
    //必须
    private Integer isDeleted;
    private Long companyId;
}
