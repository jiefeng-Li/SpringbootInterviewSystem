package com.cuit.interviewsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class InterviewSysApplication {

    public static void main(String[] args) {
        SpringApplication.run(InterviewSysApplication.class, args);
    }

}
