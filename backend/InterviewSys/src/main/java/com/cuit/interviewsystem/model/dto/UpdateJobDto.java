package com.cuit.interviewsystem.model.dto;

import com.cuit.interviewsystem.model.enums.JobTypeEnum;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UpdateJobDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -8882171134416456293L;

    /**
     * 职位标题
     */
    private String title;

    /**
     * 职位描述
     */
    private String description;

    /**
     * 职位要求
     */
    private String requirement;

    /**
     * 工作城市
     */
    private String workCity;

    /**
     * 工作性质(0全职,1兼职,2实习,3远程 )
     */
    private Integer jobType;

    /**
     * 职位标签
     */
    private String tags;

    /**
     * 最低薪资
     */
    private Integer minSalary;

    /**
     * 最高薪资
     */
    private Integer maxSalary;
}
