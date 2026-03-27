package com.cuit.interviewsystem.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 简历模板VO类
 * 用于返回简历模板信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumeTemplateVo {
    /**
     * 模板ID
     */
    private Integer id;

    /**
     * 模板名称
     */
    private String name;
}
