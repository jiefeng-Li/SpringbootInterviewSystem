package com.cuit.interviewsystem.model.dto.company;

import com.cuit.interviewsystem.model.dto.PageDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class CompanySearchPageDto extends PageDto {
    private String companyName;
    private String industry;
    private String scale;
    private String city;
}
