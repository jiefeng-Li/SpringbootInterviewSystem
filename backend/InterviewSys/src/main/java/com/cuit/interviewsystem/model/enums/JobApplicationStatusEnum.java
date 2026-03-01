package com.cuit.interviewsystem.model.enums;


import lombok.Getter;


/**
 * 状态(0待处理,1已查看,2初筛通过,3初筛不通过,4面试中,5已发Offer,6已录用,7已淘汰)
 */
@Getter
public enum JobApplicationStatusEnum {

    PENDING(0, "待处理"),
    VIEWED(1, "已查看"),
    INITIAL_SCREENING_PASSED(2, "初筛通过"),
    INITIAL_SCREENING_FAILED(3, "初筛不通过"),
    INTERVIEWING(4, "面试中"),
    OFFER_SENT(5, "已发Offer"),
    HIRED(6, "已录用"),
    ELIMINATED(7, "已淘汰");

    private final Integer status;
    private final String text;

    JobApplicationStatusEnum(Integer status, String text) {
        this.status = status;
        this.text = text;
    }


    public static JobApplicationStatusEnum getEnum(Integer status) {
        for (JobApplicationStatusEnum statusEnum : JobApplicationStatusEnum.values()) {
            if (statusEnum.getStatus().equals(status)) {
                return statusEnum;
            }
        }
        return null;
    }

    public static JobApplicationStatusEnum getEnum(String text) {
        for (JobApplicationStatusEnum statusEnum : JobApplicationStatusEnum.values()) {
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
