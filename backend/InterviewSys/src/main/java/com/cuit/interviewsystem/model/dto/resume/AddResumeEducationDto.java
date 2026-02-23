package com.cuit.interviewsystem.model.dto.resume;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
public class AddResumeEducationDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -3803317121242236843L;
    /**
     * 学校名称
     */
    @NotBlank(message = "学校名称不能为空")
    @Length(max = 64, message = "学校名称过长")
    private String school;

    /**
     * 专业
     */
    @NotBlank(message = "专业不能为空")
    @Length(max = 64, message = "专业名称过长")
    private String major;

    /**
     * 学历
     */
    @NotBlank(message = "学历不能为空")
    @Length(max = 64, message = "学历名称过长")
    private String degree;

    /**
     * 开始时间
     */
    @NotNull(message = "开始时间不能为空")
    private Date startDate;

    /**
     * 毕业时间（或至今）
     */
    @NotNull(message = "结束时间不能为空")
    private Date endDate;

    /**
     * 在校经历、荣誉等
     */
    @Length(max = 500, message = "在校经历、荣誉等不能超过500字")
    private String description;
}
