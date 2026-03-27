package com.cuit.interviewsystem.model.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserStatusUpdateDto {
    @NotNull(message = "账户状态不能为空")
    private Integer accountStatus;
}
