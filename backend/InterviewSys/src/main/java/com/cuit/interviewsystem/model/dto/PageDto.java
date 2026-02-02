package com.cuit.interviewsystem.model.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PageDto {
    //必须
    @NotNull
    private Long pageSize;
    //必须
    @NotNull
    private Long pageNum;
}
