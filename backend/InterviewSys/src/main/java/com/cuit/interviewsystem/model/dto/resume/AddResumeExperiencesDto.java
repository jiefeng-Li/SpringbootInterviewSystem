package com.cuit.interviewsystem.model.dto.resume;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Data
public class AddResumeExperiencesDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 4901113982623559975L;
    /**
     * 公司名称
     */
    @NotBlank(message = "公司名称不能为空")
    @Length(max = 64, message = "公司名称过长")
    private String company;

    /**
     * 职位名称
     */
    @NotBlank(message = "职位名称不能为空")
    @Length(max = 64, message = "职位名称过长")
    private String position;

    /**
     * 开始时间
     */
    @NotNull(message = "开始时间不能为空")
    private LocalDate startDate;

    /**
     * 结束时间（或至今）
     */
    @NotNull(message = "结束时间不能为空")
    private LocalDate endDate;

    /**
     * 工作内容、业绩等
     */
    @NotBlank(message = "工作内容不能为空")
    private String description;
}
