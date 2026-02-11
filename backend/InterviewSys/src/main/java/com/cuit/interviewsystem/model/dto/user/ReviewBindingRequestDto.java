package com.cuit.interviewsystem.model.dto.user;


import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;


@Data
public class ReviewBindingRequestDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 8789546545628306328L;
    /**
     * 主键id
     */
    @NotNull(message = "id不能为空")
    private Long id;
    /**
     * 状态(0待审,1通过,2拒绝,3取消)
     */
    @NotNull(message = "状态不能为空")
    private Integer status;
    /**
     * 审核意见
     */
    @Length(max = 1023, message = "审核意见过长")
    private String reviewNotes;
}
