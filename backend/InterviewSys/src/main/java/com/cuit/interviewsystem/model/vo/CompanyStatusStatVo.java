package com.cuit.interviewsystem.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyStatusStatVo {
    private Integer status;
    private String text;
    private Long count;
}
