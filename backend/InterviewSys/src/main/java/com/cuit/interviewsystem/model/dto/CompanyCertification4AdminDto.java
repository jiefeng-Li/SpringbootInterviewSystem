package com.cuit.interviewsystem.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CompanyCertification4AdminDto implements Serializable {
    private static final long serialVersionUID = 4290902804302016050L;

    private Long id;

    /**
     * 状态(0待审,1通过,2驳回)
     */
    private Integer status;

    /**
     * 审核意见
     */
    private String reviewNotes;
}
