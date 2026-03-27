package com.cuit.interviewsystem.model.dto.jobApplication;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
public class UpdateJobApplicationBatchDto {
    @NotEmpty(message = "投递记录ID列表不能为空")
    private List<Long> ids;

    @NotNull(message = "状态不能为空")
    private Integer status;

    @Length(max = 1024, message = "备注过长")
    private String remarks;
}
