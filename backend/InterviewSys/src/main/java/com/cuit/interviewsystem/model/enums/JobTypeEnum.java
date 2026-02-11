package com.cuit.interviewsystem.model.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum JobTypeEnum {
    /**
     * 工作性质(0全职,1兼职,2实习,3远程 )
     */
    FULL_TIME(0, "全职"),
    PART_TIME(1, "兼职"),
    INTERNSHIP(2, "实习"),
    REMOTE(3, "远程");
    @EnumValue
    private final Integer type;
    private final String text;

    JobTypeEnum(Integer type, String text) {
        this.text = text;
        this.type = type;
    }
    @Override
    public String toString() {
        return this.text;
    }
    public static JobTypeEnum getEnum(Integer type) {
        for (JobTypeEnum typeEnum : JobTypeEnum.values()) {
            if (typeEnum.type.equals(type)) {
                return typeEnum;
            }
        }
        return null;
    }

    public static JobTypeEnum getEnum(String text) {
        for (JobTypeEnum typeEnum : JobTypeEnum.values()) {
            if (typeEnum.text.equals(text)) {
                return typeEnum;
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
