package com.cuit.interviewsystem.model.vo;

import cn.hutool.json.JSONUtil;

import com.cuit.interviewsystem.exception.BusinessException;
import com.cuit.interviewsystem.exception.ErrorEnum;
import com.cuit.interviewsystem.model.entity.JobPosition;
import com.cuit.interviewsystem.model.enums.JobPositionStatusEnum;
import com.cuit.interviewsystem.model.enums.JobTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobPositionVo implements Serializable {
    private Long id;

    /**
     * 发布公司
     */
    private Long companyId;

    /**
     * 发布公司名
     */
    private String companyName;

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
    private String jobType;

    /**
     * 职位标签
     */
    private List<String> tags;

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
    private String hiringManagerName;

    /**
     * 职位状态(0草稿,1招聘中,2已暂停,3已招满,4已关闭)
     */
    private String status;

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

    private Date createTime;

    public static JobPosition voToObj(JobPositionVo jobPositionVo) {
        if (jobPositionVo == null) {
            return null;
        }
        JobPosition jobPosition = new JobPosition();
        BeanUtils.copyProperties(jobPositionVo, jobPosition);
        // 类型不同，需要转换
        jobPosition.setTags(JSONUtil.toJsonStr(jobPositionVo.getTags()));
        return jobPosition;
    }

    /**
     * 对象转封装类
     */
    public static JobPositionVo objToVo(JobPosition jobPosition) {
        if (jobPosition == null) {
            return null;
        }
        JobPositionVo jobPositionVo = new JobPositionVo();
        BeanUtils.copyProperties(jobPosition, jobPositionVo);
        // 类型不同，需要转换
        jobPositionVo.setTags(JSONUtil.toList(jobPosition.getTags(), String.class));
        return jobPositionVo;
    }
}
