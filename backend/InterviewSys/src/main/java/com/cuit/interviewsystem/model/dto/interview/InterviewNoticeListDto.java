package com.cuit.interviewsystem.model.dto.interview;


import com.cuit.interviewsystem.model.dto.PageDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class InterviewNoticeListDto extends PageDto {
    private Long companyId;
    private Long intervieweeId;
    private LocalDate start;
    private LocalDate end;
}
