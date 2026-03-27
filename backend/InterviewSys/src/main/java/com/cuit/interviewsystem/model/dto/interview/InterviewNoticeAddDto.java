package com.cuit.interviewsystem.model.dto.interview;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewNoticeAddDto {
    @NotNull(message = "投递记录ID不能为空")
    private Long jobApplicationId;
    @NotNull(message = "面试者ID不能为空")
    private Long intervieweeId;
    @NotNull(message = "公司ID不能为空")
    private Long companyId;
    @NotNull(message = "面试时间不能为空")
    private LocalDate interviewTime;
    @NotNull(message = "面试地址不能为空")
    private String interviewAddress;
    @Length(max = 500, message = "备注过长")
    private String comment;
}
