package com.cheng.springsecuritydemo.service.impl;

import com.cheng.springsecuritydemo.service.MyAccessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

@Slf4j
@Service
public class MyAccessServiceImpl implements MyAccessService {
    @Override
    public boolean hasPermission(HttpServletRequest request, Authentication authentication) {

        log.info("url:{}",request.getRequestURL());
        log.info("uri:{}",request.getRequestURI());
        // 用户主体对象
        Object obj = authentication.getPrincipal();
        if(obj instanceof UserDetails){
            UserDetails userDetails = (UserDetails) obj;
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
            return authorities.contains(new SimpleGrantedAuthority(request.getRequestURI()));
        }
        return false;
    }
}
