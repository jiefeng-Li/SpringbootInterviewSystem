package com.cuit.interviewsystem.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;



@Configuration
public class DateConfig {

    @Bean
    public Converter<String, LocalDate> localDateConverter() {
        return new Converter<>() {
            @Override
            public LocalDate convert(String source) {
                return LocalDate.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
        };
    }
}
