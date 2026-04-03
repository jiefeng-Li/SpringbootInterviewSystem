package com.cuit.interviewsystem.model.dto.offer;

import com.cuit.interviewsystem.model.dto.PageDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class OfferRecruiterPageDto extends PageDto {
    private Long recruiterId;

    private Long companyId;

    private Integer status;
}
