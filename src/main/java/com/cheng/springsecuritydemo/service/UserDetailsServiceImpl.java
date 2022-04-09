package com.cheng.springsecuritydemo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 实现UserDetailsService可以自定义用户校验
 *
 * @Author: niecheng
 * @Date: 2022/4/7 22:41
 * @Version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    private final PasswordEncoder pw;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("执行了loadUserByUsername............");

        // 1.查询数据库判断用户名是否存在，如果不存在就会抛出UsernameNotFoundException
        if(!"admin".equals(username)){
            throw new UsernameNotFoundException("用户不存在！");
        }
        // 2.把查询出来的密码（注册时已加过密）进行解析，或者把密码直接放入构造函数里
        String password = pw.encode("123");
        return new User(username,password, AuthorityUtils.commaSeparatedStringToAuthorityList("admin,normal"));
    }
}
