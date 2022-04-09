package com.cheng.springsecuritydemo.hanle;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: niecheng
 * @Date: 2022/4/7 23:33
 * @Version 1.0
 */
@RequiredArgsConstructor
@Slf4j
public class MyAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private final String url;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.sendRedirect(url);
    }
}
