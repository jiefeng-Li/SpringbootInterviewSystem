package com.cuit.interviewsystem.exception;

import com.cuit.interviewsystem.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public Result<?> businessExceptionHandler(RuntimeException e) {
        log.error("RuntimeException", e);
        return Result.error(ErrorEnum.SYSTEM_ERROR, "系统错误");
    }
}
