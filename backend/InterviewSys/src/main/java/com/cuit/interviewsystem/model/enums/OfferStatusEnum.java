package com.cuit.interviewsystem.model.enums;

import lombok.Getter;

@Getter
public enum OfferStatusEnum {
    PENDING(0, "待确认"),
    ACCEPTED(1, "已接受"),
    REJECTED(2, "已拒绝"),
    EXPIRED(3, "已过期"),
    WITHDRAWN(4, "已撤回"),
    CANCELED(5, "已取消");

    private final Integer status;
    private final String text;

    OfferStatusEnum(Integer status, String text) {
        this.status = status;
        this.text = text;
    }

    public static OfferStatusEnum getEnum(Integer status) {
        for (OfferStatusEnum item : OfferStatusEnum.values()) {
            if (item.status.equals(status)) {
                return item;
            }
        }
        return null;
    }
}
