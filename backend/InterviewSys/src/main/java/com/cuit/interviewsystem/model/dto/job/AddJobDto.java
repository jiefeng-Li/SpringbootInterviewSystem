package com.cuit.interviewsystem.model.dto.job;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class AddJobDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 3449098264348600971L;

    /**
     * 职位标题
     */
    @NotBlank(message = "职位标题不能为空")
    private String title;

    /**
     * 职位描述
     */
    @NotBlank(message = "职位描述不能为空")
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
    private List<String> tags;

    /**
     * 最低薪资
     */
    @Min(value = 0, message = "最低薪资不能小于0")
    private Integer minSalary;

    /**
     * 最高薪资
     */
    private Integer maxSalary;

    /**
     * 经验要求
     */
    private String experience;

    /**
     * 学历要求
     */
    private String education;

    /**
     * 招聘人数
     */
    @Min(value = 1, message = "招聘人数不能小于1")
    private Integer headcount;

    /**
     * 职位状态(0草稿,1招聘中,2已暂停,3已招满,4已关闭)
     */
    private Integer status;
}
