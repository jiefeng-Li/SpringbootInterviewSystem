package com.cuit.interviewsystem.model.dto.company;

import com.cuit.interviewsystem.model.dto.PageDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminCompanySearchPageDto extends PageDto {    private String companyName;
    private String industry;
    private String scale;
    private String city;
    private Integer status;
}
