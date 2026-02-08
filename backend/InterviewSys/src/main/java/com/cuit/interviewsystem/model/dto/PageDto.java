package com.cuit.interviewsystem.model.dto;



import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageDto {
    //必须
    @NotNull(message = "页大小不能为空")
    @Min(value = 1, message = "页大小不能小于1")
    @Max(value = 1000, message = "页大小不能大于1000")
    private Long pageSize;
    //必须
    @NotNull(message = "页数不能为空")
    @Min(value = 1, message = "页数不能小于1")
    private Long pageNum;
}
