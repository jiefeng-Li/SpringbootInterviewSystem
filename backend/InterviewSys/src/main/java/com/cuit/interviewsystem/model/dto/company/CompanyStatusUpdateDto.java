package com.cuit.interviewsystem.model.dto.company;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CompanyStatusUpdateDto {
    @NotNull(message = "公司状态不能为空")
    private Integer status;
}
