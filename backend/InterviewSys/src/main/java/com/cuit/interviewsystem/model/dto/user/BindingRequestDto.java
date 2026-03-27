package com.cuit.interviewsystem.model.dto.user;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

@Data
public class BindingRequestDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -6479636872355358501L;

    /**
     * 目标公司ID
     */
    @NotNull
    private Long companyId;

    /**
     * 申请备注
     */
    @Nullable
    @Length(max = 1024, message = "申请理由过长")
    private String applicationNotes;
}
