package com.cuit.interviewsystem.model.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum JobPositionStatusEnum {
    /**
     * 职位状态(0草稿,1招聘中,2已暂停,3已招满,4已关闭)
     */
    DRAFT(0, "草稿"),
    RECRUITING(1, "招聘中"),
    PAUSED(2, "已暂停"),
    FULLY_RECRUITED(3, "已招满"),
    CLOSED(4, "已关闭");
    @EnumValue
    private final Integer status;
    private final String text;
    JobPositionStatusEnum(Integer status, String text) {
        this.text = text;
        this.status = status;
    }
    @Override
    public String toString() {
        return this.text;
    }

    public static JobPositionStatusEnum getEnum(Integer status) {
        for (JobPositionStatusEnum statusEnum : JobPositionStatusEnum.values()) {
            if (statusEnum.status.equals(status)) {
                return statusEnum;
            }
        }
        return null;
    }

    public static JobPositionStatusEnum getEnum(String text) {
        for (JobPositionStatusEnum statusEnum : JobPositionStatusEnum.values()) {
            if (statusEnum.text.equals(text)) {
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
