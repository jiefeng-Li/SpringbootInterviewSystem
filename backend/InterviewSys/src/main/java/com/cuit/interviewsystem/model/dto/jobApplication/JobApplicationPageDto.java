package com.cuit.interviewsystem.model.dto.jobApplication;

import com.cuit.interviewsystem.model.dto.PageDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class JobApplicationPageDto extends PageDto {
    @NotNull(message = "公司ID不能为空")
    private Long companyId;
    private Long jobPositionId;
    private Integer status; // job application status
    private List<Integer> statusList;
}
