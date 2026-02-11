package com.cuit.interviewsystem.model.dto.job;


import com.cuit.interviewsystem.model.dto.PageDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class JobSearchPageDto extends PageDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -3645809734074569384L;

    private Long companyId;
    private String workCity;
    private Integer jobType;
    private List<String> tags;
    private Integer minSalary;
    private Integer maxSalary;
    private String experience;
    private String education;
    private Integer status;
    /**
     * 公司规模
     * model.entity: Company.scale
     */
    private String scale;

    private Boolean descByCreateTime = true;
    private Boolean descByPublishTime = true;
    private Boolean descByHeadCount;
}
