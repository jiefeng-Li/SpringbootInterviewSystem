package com.cuit.interviewsystem.model.enums;


import lombok.Getter;

@Getter
public enum UserBindingStatusEnum {
    REVIEWING(0, "待审"),
    PASS(1, "通过"),
    REFUSE(2, "驳回"),
    CANCEL(3, "取消");
    private final Integer status;
    private final String text;

    UserBindingStatusEnum(Integer status, String text) {
        this.status = status;
        this.text = text;
    }

    public static UserBindingStatusEnum getEnum(Integer status) {
        for (UserBindingStatusEnum statusEnum : UserBindingStatusEnum.values()) {
            if (statusEnum.getStatus().equals(status)) {
                return statusEnum;
            }
        }
        return null;
    }

    public static UserBindingStatusEnum getEnum(String text) {
        for (UserBindingStatusEnum statusEnum : UserBindingStatusEnum.values()) {
            if (statusEnum.getText().equals(text)) {
                return statusEnum;
            }
        }
        try {
            int status = Integer.parseInt(text);
            return getEnum(status);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
