package com.cuit.interviewsystem.model.enums;


import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

@Getter
public enum UserRoleEnum {
    JOB_SEEKER("求职者", "JOB_SEEKER"),
    RECRUITER("招聘者", "RECRUITER"),
    COMP_ADMIN("企业管理员", "COMP_ADMIN"),
    SYS_ADMIN("系统管理员", "SYS_ADMIN");
    private final String text;
    private final String value;

    UserRoleEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public static UserRoleEnum getEnumByValue(String value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (UserRoleEnum u: UserRoleEnum.values()) {
            if (u.value.equals(value)) {
                return u;
            }
        }
        return null;
    }

    public static UserRoleEnum getEnumByText(String text) {
        if (ObjUtil.isEmpty(text)) {
            return null;
        }
        for (UserRoleEnum u: UserRoleEnum.values()) {
            if (u.text.equals(text)) {
                return u;
            }
        }
        return null;
    }

    public static UserRoleEnum getRole(String str) {
        UserRoleEnum valueRole = getEnumByValue(str);
        if (valueRole != null)
            return valueRole;
        return getEnumByText(str);
    }
}
