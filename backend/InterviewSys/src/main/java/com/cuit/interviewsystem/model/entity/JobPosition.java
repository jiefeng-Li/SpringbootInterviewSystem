package com.cuit.interviewsystem.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;

import com.cuit.interviewsystem.model.enums.JobPositionStatusEnum;
import com.cuit.interviewsystem.model.enums.JobTypeEnum;
import lombok.Data;

/**
 * 职位发布表
 * @TableName t_job_position
 */
@TableName(value ="t_job_position")
@Data
public class JobPosition {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 发布公司
     */
    private Long companyId;

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
    private JobTypeEnum jobType;

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
    private Integer headcount;

    /**
     * 负责人
     */
    private Long hiringManagerId;

    /**
     * 职位状态(0草稿,1招聘中,2已暂停,3已招满,4已关闭)
     */
    private JobPositionStatusEnum status;

    /**
     * 发布时间
     */
    private Date publishTime;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 投递量
     */
    private Integer applyCount;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer isDeleted;
    private Date createTime;
    private Date updateTime;
}