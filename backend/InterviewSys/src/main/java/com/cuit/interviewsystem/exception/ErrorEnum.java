package com.cuit.interviewsystem.exception;

import lombok.Getter;

@Getter
public enum ErrorEnum {
    SUCCESS(200, "ok"),
    PARAMS_ERROR(400, "请求参数错误"),
    NOT_LOGIN_ERROR(401, "未登录"),
    NOT_FOUND_ERROR(404, "请求数据不存在"),
    SYSTEM_ERROR(500, "系统内部异常");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 异常信息
     */
    private final String message;

    ErrorEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
