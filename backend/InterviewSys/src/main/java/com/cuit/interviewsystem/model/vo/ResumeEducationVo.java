package com.cuit.interviewsystem.model.vo;

import com.cuit.interviewsystem.model.entity.ResumeEducation;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;

@Data
public class ResumeEducationVo {
    private Long id;

    /**
     * 学校名称
     */
    private String school;

    /**
     * 专业
     */
    private String major;

    /**
     * 学历
     */
    private String degree;

    /**
     * 开始时间
     */
    private Date startDate;

    /**
     * 毕业时间（或至今）
     */
    private Date endDate;

    /**
     * 在校经历、荣誉等
     */
    private String description;
}
