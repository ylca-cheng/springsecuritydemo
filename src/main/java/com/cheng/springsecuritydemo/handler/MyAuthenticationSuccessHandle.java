package com.cheng.springsecuritydemo.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: niecheng
 * @Date: 2022/4/7 23:25
 * @Version 1.0
 */
@RequiredArgsConstructor
@Slf4j
public class MyAuthenticationSuccessHandle implements AuthenticationSuccessHandler {
    private final String url;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        User user = (User) authentication.getPrincipal();
        log.info(user.getUsername());
        log.info(user.getPassword());
        log.info(user.getAuthorities().toString());
        response.sendRedirect(url);
    }
}
