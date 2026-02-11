package com.cuit.interviewsystem.model.dto.company;


import com.cuit.interviewsystem.model.dto.PageDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class CertificationRecordPageDto extends PageDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -1765888811758112811L;
    private Long companyId;
    private Long reviewBy;
    private Integer status;
    //默认为0
    private Integer isDeleted = 0;
}
