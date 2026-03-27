package com.cuit.interviewsystem.constant;

import lombok.Getter;

/**
 * 简历模板常量类
 * 用于管理简历模板ID与模板文件的映射关系
 */
@Getter
public enum ResumeTemplate {

    /**
     * 模板1
     */
    BUSINESS(1, "resume/business.html", "模板1"),

    /**
     * 模板2
     */
    MODERN(2, "resume/modern.html", "模板2"),

    /**
     * 模板3
     */
    SIMPLE(3, "resume/simple.html", "模板3"),

    /**
     * 模板4
     */
    TECH(4, "resume/tech.html", "模板4");

    /**
     * 模板ID
     */
    private final Integer id;

    /**
     * 模板文件路径（相对于templates目录）
     */
    private final String templatePath;

    /**
     * 模板名称
     */
    private final String name;

    ResumeTemplate(Integer id, String templatePath, String name) {
        this.id = id;
        this.templatePath = templatePath;
        this.name = name;
    }

    /**
     * 根据模板ID获取模板
     * @param id 模板ID
     * @return 简历模板枚举
     */
    public static ResumeTemplate getById(Integer id) {
        if (id == null) {
            return BUSINESS; // 默认返回模板1
        }
        for (ResumeTemplate template : values()) {
            if (template.getId().equals(id)) {
                return template;
            }
        }
        return BUSINESS; // 找不到则返回默认模板
    }

    /**
     * 根据模板ID获取模板路径
     * @param id 模板ID
     * @return 模板路径
     */
    public static String getTemplatePathById(Integer id) {
        return getById(id).getTemplatePath();
    }
}
