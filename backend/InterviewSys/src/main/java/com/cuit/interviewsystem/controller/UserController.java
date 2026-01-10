package com.cuit.interviewsystem.controller;


import com.cuit.interviewsystem.model.dto.UserRegisterDto;
import com.cuit.interviewsystem.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
public class UserController {
    @Resource
    private UserService userService;


    @PostMapping("/user")
    public void userRegister(UserRegisterDto userRegisterDto) {
        userService.userRegister(userRegisterDto);
    }
}
