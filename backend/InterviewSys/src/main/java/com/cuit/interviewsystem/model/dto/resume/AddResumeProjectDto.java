package com.cuit.interviewsystem.model.dto.resume;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Data
public class AddResumeProjectDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -5945208393402605293L;
    /**
     * 项目名称
     */
    @NotBlank(message = "项目名称不能为空")
    private String name;

    /**
     * 项目描述
     */
    @NotBlank(message = "项目描述不能为空")
    @Length(max = 500, message = "项目描述不能超过500字")
    private String description;

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
}
