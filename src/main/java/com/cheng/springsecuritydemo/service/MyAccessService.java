package com.cheng.springsecuritydemo.service;

import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

public interface MyAccessService {
    boolean hasPermission(HttpServletRequest request, Authentication authentication);
}
