package com.cuit.interviewsystem.model.dto.jobApplication;


import com.cuit.interviewsystem.model.dto.PageDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class OwnJobApplicationPageDto extends PageDto {
    private Long jobPositionId;
    private Long userId;
    private Long companyId;
    private Long resumeId;
    private Integer status;
}
