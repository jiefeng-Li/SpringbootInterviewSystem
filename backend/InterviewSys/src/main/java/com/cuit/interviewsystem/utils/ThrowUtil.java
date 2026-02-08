package com.cuit.interviewsystem.utils;


import com.cuit.interviewsystem.exception.BusinessException;
import com.cuit.interviewsystem.exception.ErrorEnum;

public class ThrowUtil <T> {
    public static void throwIfTrue(boolean condition, RuntimeException runtimeException) {
        if (condition) {
            throw runtimeException;
        }
    }

    public static void throwIfTrue(boolean condition, BusinessException be) {
        if (condition) {
            throw be;
        }
    }

    public static void throwIfTrue(boolean condition, ErrorEnum errorEnum) {
        if (condition) {
            throw new BusinessException(errorEnum);
        }
    }

    /**
     *
     * @param condition 条件
     * @param errorEnum 提供错误码
     * @param message 错误信息
     */
    public static void throwIfTrue(boolean condition, ErrorEnum errorEnum, String message) {
        if (condition) {
            throw new BusinessException(errorEnum.getCode(), message);
        }
    }
    /*
        public static void throwIfTrue(boolean condition, int code, String message) {
        if (condition) {
            throw new BusinessException(code, message);
        }
    }
     */
}
