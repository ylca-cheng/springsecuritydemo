package com.cheng.springsecuritydemo.config;

import com.cheng.springsecuritydemo.hanle.MyAuthenticationFailureHandler;
import com.cheng.springsecuritydemo.hanle.MyAuthenticationSuccessHandle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @Author: niecheng
 * @Date: 2022/4/7 22:39
 * @Version 1.0
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 表单提交
        http.formLogin()
                // 当发现/login时认为是登录，必须和表单提交的地址一样，去执行UserDetailsServiceImpl
                .loginProcessingUrl("/login")
                // 自定义登录页面
                .loginPage("/login.html")
                // 登录成功后跳转页面，必须是post请求 successForwardUrl只能跳转系统内的地址，因为是转发请求
                // .successForwardUrl("/toMain")
                //successHandler可以自定义跳转逻辑
                .successHandler(new MyAuthenticationSuccessHandle("https://www.baidu.com"))
                // 登录失败后跳转页面，必须是post请求
//                .failureForwardUrl("/toError")
                .failureHandler(new MyAuthenticationFailureHandler("/error.html"))
                // 自定义用户名参数名(当前端页面登录页面表单里的用户名不是username的时候需要定义)
                .usernameParameter("username111")
                .passwordParameter("password111");

        // 授权认证
        http.authorizeRequests()
                // error.html不需要被认证
                .antMatchers("/login.html").permitAll()
                // login.html不需要被认证
                .antMatchers("/error.html").permitAll()
//                .antMatchers("/js/**","/images/**","/css/**").permitAll()
//                .antMatchers("/**/*.png").permitAll()
                .regexMatchers(".+[.]png").permitAll()
//                .regexMatchers(HttpMethod.POST,"/demo").permitAll()
//                .mvcMatchers("/demo").servletPath("/xxx").permitAll()
//                .antMatchers("/demo").permitAll()
                // 所有请求都必须认证
                .anyRequest().authenticated();

        // 关闭csrf
        http.csrf().disable();
    }
}
