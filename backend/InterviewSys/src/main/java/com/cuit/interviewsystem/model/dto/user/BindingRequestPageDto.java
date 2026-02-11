package com.cuit.interviewsystem.model.dto.user;




import com.cuit.interviewsystem.model.dto.PageDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;


@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class BindingRequestPageDto extends PageDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -6431257916620217295L;
    @NotNull(message = "公司ID不能为空")
    private Long companyId;
    private Integer status;
    private Date start;
    private Date end;
    private Integer isDeleted = 0;
}
