package com.cuit.interviewsystem.model.dto.offer;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class OfferAddDto {
    @NotNull(message = "投递记录ID不能为空")
    private Long jobApplicationId;

    @NotNull(message = "公司ID不能为空")
    private Long companyId;

    @NotNull(message = "求职者ID不能为空")
    private Long jobSeekerId;

    @NotNull(message = "招聘者ID不能为空")
    private Long recruiterId;

    private Long interviewNoticeId;

    @NotNull(message = "职位ID不能为空")
    private Long jobPositionId;

    private LocalDate expectedEntryDate;

    @DecimalMin(value = "0.0", inclusive = true, message = "月薪不能为负数")
    private BigDecimal salaryMonthly;

    @Max(value = 24, message = "年薪月数不合理")
    private Integer salaryMonthCount;

    @Max(value = 24, message = "试用期月数不合理")
    private Integer probationMonths;

    @NotNull(message = "Offer截止时间不能为空")
    @Future(message = "Offer截止时间必须晚于当前日期")
    private LocalDate offerDeadline;

    @Length(max = 500, message = "福利说明过长")
    private String welfare;

    @Length(max = 500, message = "备注过长")
    private String remark;
}
