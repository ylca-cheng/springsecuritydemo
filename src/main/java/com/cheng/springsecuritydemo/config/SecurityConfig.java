package com.cheng.springsecuritydemo.config;

import com.cheng.springsecuritydemo.handler.MyAccessDeniedHandle;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

/**
 * @Author: niecheng
 * @Date: 2022/4/7 22:39
 * @Version 1.0
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private  MyAccessDeniedHandle myAccessDeniedHandle;

    @Autowired
    private  DataSource dataSource;

    @Autowired
    private  PersistentTokenRepository persistentTokenRepository;

    @Qualifier("userDetailsServiceImpl")
    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository(){
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        // 第一次启动是创建表，第二次启动需要注释掉，否则会报错
//        jdbcTokenRepository.setCreateTableOnStartup(true);
        // 设置数据源
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 表单提交
        http.formLogin()
                // 当发现/login时认为是登录，必须和表单提交的地址一样，去执行UserDetailsServiceImpl
                .loginProcessingUrl("/login")
                // 自定义登录页面
//                .loginPage("/login.html")
                .loginPage("/showLogin")
                // 登录成功后跳转页面，必须是post请求 successForwardUrl只能跳转系统内的地址，因为是转发请求
                 .successForwardUrl("/toMain")
                //successHandler可以自定义跳转逻辑
//                .successHandler(new MyAuthenticationSuccessHandle("https://www.baidu.com"))
                // 登录失败后跳转页面，必须是post请求
                .failureForwardUrl("/toError")
//                .failureHandler(new MyAuthenticationFailureHandler("/error.html"))
                // 自定义用户名参数名(当前端页面登录页面表单里的用户名不是username的时候需要定义)
                .usernameParameter("username111")
                .passwordParameter("password111");

        // 授权认证
        http.authorizeRequests()
                // login.html不需要被认证
//                .antMatchers("/login.html").permitAll()
//                .antMatchers("/login.html").access("permitAll()")
                .antMatchers("/showLogin").permitAll()
                // error.html不需要被认证
                .antMatchers("/error.html").permitAll()
//                .antMatchers("/js/**","/images/**","/css/**").permitAll()
//                .antMatchers("/**/*.png").permitAll()
                .regexMatchers(".+[.]png").permitAll()
//                .regexMatchers(HttpMethod.POST,"/demo").permitAll()
//                .mvcMatchers("/demo").servletPath("/xxx").permitAll()
//                .antMatchers("/demo").permitAll()
                // 配置单一权限
//                .antMatchers("/main1.html").hasAuthority("admin")
                // 配置多个权限
//                .antMatchers("/main1.html").hasAnyAuthority("admin","admiN")
                // 角色权限控制，只有角色是abc的可访问main1.html
//                .antMatchers("/main1.html").hasRole("abc")
//                .antMatchers("/main1.html").access("hasRole('abc')")
                // 分配多个角色，都是精确匹配
//                .antMatchers("/main1.html").hasAnyRole("abc","abC")
                // 限制ip权限,只有127.0.0.1 可以访问main1.html
//                .antMatchers("/main1.html").hasIpAddress("127.0.0.1")
                // 所有请求在认证后都可访问
                .anyRequest().authenticated();
                // myAccessServiceImpl,是实现类的类名首字母小写，因为通过@Service 注册到spring中的实例名默认是类名首字母小写
//                .anyRequest().access("@myAccessServiceImpl.hasPermission(request,authentication)");

        // 关闭csrf
//        http.csrf().disable();

        http.exceptionHandling().accessDeniedHandler(myAccessDeniedHandle);

        // 记住我功能需要持久到数据库或者内存中
        http.rememberMe()
                // 必须的配置，不然token验证会出错，因为没法判断用户
                .userDetailsService(userDetailsService)
                // token过期时间，单位秒，默认是两周
                .tokenValiditySeconds(60)
                // 自定义记住我 参数名称，默认是remember-me
//                .rememberMeParameter("rememberMe")
                // 持久化对象
                .tokenRepository(persistentTokenRepository);

        http.logout()
                // 自定义退出uri
//                .logoutUrl("/user/logout")
                // 定义退出访问的uri，默认是/login.html?logout,如果不想退出后路径后面有logout，就可以自定义这个路径
        .logoutSuccessUrl("/login.html");
    }
}
