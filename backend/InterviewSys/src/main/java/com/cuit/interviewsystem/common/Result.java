package com.cuit.interviewsystem.common;


import com.cuit.interviewsystem.exception.ErrorEnum;
import lombok.Data;

@Data
public class Result<T> {
    private int code;
    private String message;
    private Object data;

    private Result(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    private Result(int code, T data) {
        this(code, data, "");
    }

    public Result(ErrorEnum errorEnum) {
        this(errorEnum.getCode(), null, errorEnum.getMessage());
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(ErrorEnum.SUCCESS.getCode(), data, "success");
    }

    public static <T> Result<T> success(T data, String message) {
        return new Result<>(ErrorEnum.SUCCESS.getCode(), data, message);
    }

    public static <T> Result<T> error(ErrorEnum errorEnum) {
        return new Result<>(errorEnum.getCode(), null, errorEnum.getMessage());
    }

    public static <T> Result<T> error(ErrorEnum errorEnum, String message) {
        return new Result<>(errorEnum.getCode(), null, message);
    }

    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, null, message);
    }

    public static <T> Result<T> build(int code, T data, String message) {
        return new Result<>(code, data, message);
    }
}
