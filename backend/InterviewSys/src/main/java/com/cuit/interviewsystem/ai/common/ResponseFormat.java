package com.cuit.interviewsystem.ai.common;

import lombok.Getter;
import lombok.Setter;

// 使用 Java 类定义响应格式
@Getter
@Setter
public class ResponseFormat {
    // 一个双关语响应（始终必需）
    private String punnyResponse;

    /*
    // 如果可用的话，关于天气的任何有趣信息
    private String weatherConditions;

    // Getters and Setters
    public String getPunnyResponse() {
        return punnyResponse;
    }

    public void setPunnyResponse(String punnyResponse) {
        this.punnyResponse = punnyResponse;
    }

    public String getWeatherConditions() {
        return weatherConditions;
    }

    public void setWeatherConditions(String weatherConditions) {
        this.weatherConditions = weatherConditions;
    }*/
}