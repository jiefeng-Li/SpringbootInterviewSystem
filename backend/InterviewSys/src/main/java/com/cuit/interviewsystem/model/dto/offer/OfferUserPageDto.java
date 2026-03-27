package com.cuit.interviewsystem.model.dto.offer;

import com.cuit.interviewsystem.model.dto.PageDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class OfferUserPageDto extends PageDto {
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    private Integer status;
}
