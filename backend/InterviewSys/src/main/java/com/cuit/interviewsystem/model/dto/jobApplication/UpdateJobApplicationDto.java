package com.cuit.interviewsystem.model.dto.jobApplication;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UpdateJobApplicationDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 4206390702109659095L;

    @NotNull
    private Long id;
    /**
     * 状态(0待处理,1已查看,2初筛通过,3初筛不通过,4面试中,5已发Offer,6已录用,7已淘汰)
     */
    private Integer status;

    /**
     * HR备注
     */
    @Length(max = 1024, message = "备注过长")
    private String remarks;

}
