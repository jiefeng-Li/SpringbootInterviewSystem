package com.cuit.interviewsystem.model.dto.offer;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class OfferStatusUpdateDto {
    @NotNull(message = "Offer记录ID不能为空")
    private Long id;

    /**
     * 仅允许：1-已接受，2-已拒绝
     */
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Length(max = 500, message = "拒绝原因过长")
    private String rejectReason;
}
