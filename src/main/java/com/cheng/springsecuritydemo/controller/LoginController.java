package com.cheng.springsecuritydemo.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

/**
 * @Author: niecheng
 * @Date: 2022/4/7 22:17
 * @Version 1.0
 */
@Controller
public class LoginController {

    /*@RequestMapping("/login")
    public String login(){
        System.out.println("执行登录方法");
        return "redirect:main.html";
    }*/

    // 必须带有前缀ROLE_
    @Secured("ROLE_abc")
//    @PreAuthorize("hasRole('abc')")
    @RequestMapping("/toMain")
    public String toMain(){
        return "redirect:main.html";
    }

    @RequestMapping("/toError")
    public String toError(){
        return "redirect:error.html";
    }

    @RequestMapping("/demo")
    public String demo(){
        return "demo";
    }

    //查出用户数据，在页面展示
    @RequestMapping("/main")
    public String success(Map<String,Object> map){
        map.put("hello","<h1>你好</h1>");
        map.put("users", Arrays.asList("zhangsan","lisi","wangwu"));
        return "main";
    }

    @RequestMapping("/showLogin")
    public String showLogin(){
        return "login";
    }
}
