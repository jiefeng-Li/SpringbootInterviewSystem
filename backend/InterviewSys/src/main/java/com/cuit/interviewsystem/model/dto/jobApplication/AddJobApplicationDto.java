package com.cuit.interviewsystem.model.dto.jobApplication;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

@Data
public class AddJobApplicationDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -6623929999767774295L;

    /**
     * 投递的公司
     */
    @NotNull(message = "公司不能为空")
    private Long companyId;

    /**
     * 职位ID
     */
    @NotNull(message = "职位不能为空")
    private Long jobPositionId;

    /**
     * 求职者用户ID
     */
    @NotNull(message = "用户不能为空")
    private Long userId;

    /**
     * 投递时使用的简历ID
     */
    @NotNull(message = "简历不能为空")
    private Long resumeId;

    /**
     * 求职信
     */
    @Length(max = 1024, message = "求职信过长")
    private String coverLetter;
}
