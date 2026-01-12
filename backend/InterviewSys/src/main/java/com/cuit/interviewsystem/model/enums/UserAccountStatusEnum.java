package com.cuit.interviewsystem.model.enums;


import lombok.Getter;

@Getter
public enum UserAccountStatusEnum {
    /**
     * 账户状态(0禁用,1正常,2锁定)
     */

    BANED(0, "禁用"),
    NORMAL(1, "正常"),
    LOCKED(2, "锁定");
    private final Integer status;
    private final String text;

    UserAccountStatusEnum(Integer status, String text) {
        this.status = status;
        this.text = text;
    }

    public static UserAccountStatusEnum getEnumByStatus(Integer status) {
        for (UserAccountStatusEnum statusEnum : UserAccountStatusEnum.values()) {
            if (statusEnum.getStatus().equals(status)) {
                return statusEnum;
            }
        }
        return null;
    }
}
