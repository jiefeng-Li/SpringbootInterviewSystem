package com.cuit.interviewsystem.model.enums;


import lombok.Getter;

/**
 * 状态(0待审,1正常,2禁用,3注销)
 */
@Getter
public enum CompanyStatusEnum {
    REVIEWING(0, "待审"),
    NORMAL(1, "正常"),
    BANED(2, "禁用"),
    DEREGISTER(3, "注销");
    private final Integer status;
    private final String text;

    CompanyStatusEnum(Integer status, String text) {
        this.status = status;
        this.text = text;
    }

    public static CompanyStatusEnum getEnum(Integer status) {
        for (CompanyStatusEnum statusEnum : CompanyStatusEnum.values()) {
            if (statusEnum.getStatus().equals(status)) {
                return statusEnum;
            }
        }
        return null;
    }

    public static CompanyStatusEnum getEnum(String text) {
        for (CompanyStatusEnum statusEnum : CompanyStatusEnum.values()) {
            if (statusEnum.getText().equals(text)) {
                return statusEnum;
            }
        }
        return null;
    }
}
