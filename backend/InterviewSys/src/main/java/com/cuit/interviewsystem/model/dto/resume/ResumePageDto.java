package com.cuit.interviewsystem.model.dto.resume;


import com.cuit.interviewsystem.model.dto.PageDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class ResumePageDto extends PageDto {
    @NotNull(message = "用户id不能为空")
    private Long userId;
}
