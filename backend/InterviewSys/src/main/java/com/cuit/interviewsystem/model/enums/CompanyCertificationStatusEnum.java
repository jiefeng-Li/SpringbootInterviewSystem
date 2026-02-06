package com.cuit.interviewsystem.model.enums;

import lombok.Getter;

@Getter
public enum CompanyCertificationStatusEnum {
    REVIEWING(0, "待审"),
    PASS(1, "通过"),
    REFUSE(2, "驳回"),
    CANCEL(3, "取消");
    private final Integer status;
    private final String text;

    CompanyCertificationStatusEnum(Integer status, String text) {
        this.status = status;
        this.text = text;
    }

    public static CompanyCertificationStatusEnum getEnum(Integer status) {
        for (CompanyCertificationStatusEnum statusEnum : CompanyCertificationStatusEnum.values()) {
            if (statusEnum.getStatus().equals(status)) {
                return statusEnum;
            }
        }
        return null;
    }

    public static CompanyCertificationStatusEnum getEnum(String text) {
        for (CompanyCertificationStatusEnum statusEnum : CompanyCertificationStatusEnum.values()) {
            if (statusEnum.getText().equals(text)) {
                return statusEnum;
            }
        }
        return null;
    }
}
