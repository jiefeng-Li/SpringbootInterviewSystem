package com.cuit.interviewsystem.model.dto.company;


import com.cuit.interviewsystem.model.dto.PageDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class CompanyUserPageDto extends PageDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -6009490512693809176L;
    private Long companyId;
    private String role;
}
