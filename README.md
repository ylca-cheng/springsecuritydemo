# Spring Security 练习demo


## 1、使用
首先引入依赖
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```
权限配置：  
实现**WebSecurityConfigurerAdapter**，重写**config**配置权限

具体可以参考本例中的**SecurityConfig**类

## 2、注解的使用

在启动类上添加注解 @EnableGlobalMethodSecurity(securedEnabled = true)
```java
@EnableGlobalMethodSecurity(securedEnabled = true)
@SpringBootApplication
public class SpringsecuritydemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringsecuritydemoApplication.class, args);
    }

}
```
然后在就可以在方法上使用注解，一般是加在控制层上

例如：
```Java
//    @Secured("ROLE_abC")
@PreAuthorize("hasRole('abc')")
@RequestMapping("/toMain")
public String toMain(){
    return "redirect:main.html";
}
```

### 2.1 常用注解说明
1、@Secured
> 用在方法和类上，可以用来配置角色权限，例如@Secured("ROLE_abC")  
> 注意ROLE_ 前缀不能少,否则报错org.springframework.security.access.AccessDeniedException: 不允许访问  
> 角色名是精确匹配的

2、@PreAuthorize
> 表示在访问方法或类执行之前先判断权限  
> 注解的参数和access()方法的参数取值相同，都是权限表达式

3、@PostAuthorize
> 表示在访问方法或类执行之后先判断权限  

# 3、记住我功能
> 记住我功能需要持久化到数据库，或者内存

选择持久化到数据库配置：
```java
// 记住我功能需要持久到数据库或者内存中
        http.rememberMe()
        // 必须的配置，不然token验证会出错，因为没法判断用户
        .userDetailsService(userDetailsService)
        // token过期时间，单位秒，默认是两周
        .tokenValiditySeconds(60)
        // 自定义记住我 参数名称，默认是remember-me
//       .rememberMeParameter("rememberMe")
        // 持久化对象
        .tokenRepository(persistentTokenRepository);
```
登录表单：
```html
<form action="/login" method="post">
    用户名：<input type="text" name="username111"/><br/>
    密码：<input type="text" name="password111"/><br/>
    记住我：<input type="checkbox" name="remember-me" value="true"/><br/>
    <input type="submit" value="登录"/><br/>
</form>
```
persistentTokenRepository 配置：
```java
    @Autowired
    private  DataSource dataSource;
    @Autowired
    private  PersistentTokenRepository persistentTokenRepository;

    @Bean
    public PersistentTokenRepository persistentTokenRepository(){
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        // 第一次启动是创建表，第二次启动需要注释掉，否则会报错
//        jdbcTokenRepository.setCreateTableOnStartup(true);
        // 设置数据源
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }
```
# 4、支持thymeleaf
引入依赖：
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
<!--thymeleaf-extras-springsecurityx x是版本号，这里用的是5-->
<dependency>
    <groupId>org.thymeleaf.extras</groupId>
    <artifactId>thymeleaf-extras-springsecurity5</artifactId>
</dependency>
```
例子：
```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity5">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
登录账户：<span sec:authentication="name"></span><br/>
登录账户：<span sec:authentication="principal.username"></span><br/>
凭证：<span sec:authentication="credentials"></span><br/>
权限和角色：<span sec:authentication="authorities"></span><br/>
客户端地址：<span sec:authentication="details.remoteAddress"></span><br/>
sessionId：<span sec:authentication="details.sessionId"></span><br/>
</body>
</html>
```
命名空间xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity5" 是必须的

sec:authentication 是 UsernamePasswordAuthenticationToken，
UsernamePasswordAuthenticationToken里有的都可以展示。

principal 是指用户主体，实际上就是UserDetail的实例对象

details 是 WebAuthenticationDetails的实例对象

# 5、登录退出功能
前端页面：
```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<b1>登录成功！</b1>
<a href="main1.html">跳转</a> <br>

<a href="/logout">退出</a>
</body>
</html>
```
**/logout**是spring security自带的一个退出路径，不用另外再写一个controller

如果想要自定义这个路径，可以参考以下配置：
```java
http.logout()
// 自定义退出uri
//     .logoutUrl("/user/logout")
// 定义退出访问的uri，默认是/login.html?logout,如果不想退出后路径后面有logout，就可以自定义这个路径
    .logoutSuccessUrl("/login.html");
```
# 6、CSRF
> CSRF(Cross-Site request forgery)跨站请求伪造，也被称为"OneClick Attack" 
> 或者Session Riding。通过伪造用户请求访问受信任站点的非法请求访问。

跨域：只要网络协议，ip地址，端口中的任何一个不相同就是跨域请求。

客户端与服务进行交互时，由于http协议本身是无状态协议，所有引进了cookie进行记录
客户端身份。在cookie中会放session id 用来识别客户端身份的。在跨域的情况下，
session id 可能被第三方恶意劫持，通过session ID 向服务端发起请求时，服务端
会认为这个请求是合法的，可能发生很多意想不到的事情。

## spring security 中的csrf
从spring security4开始CSRF防护默认开启。默认会拦截请求，进行csrf处理，
csrf为保证不是其他第三方网站访问，要求访问时携带参数名为_csrf值为token
（token在服务端产生）的内容，如果token和服务端的token匹配成功，则正常访问。

1. 编写controller
```java
    @RequestMapping("/showLogin")
    public String showLogin(){
        return "login";
    }
```
在templates下添加login.html
```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<form action="/login" method="post">
    <input type="hidden" th:value="${_csrf.token}" name="_csrf" th:if="${_csrf}" />
    用户名：<input type="text" name="username111"/><br/>
    密码：<input type="text" name="password111"/><br/>
    记住我：<input type="checkbox" name="remember-me" value="true"/><br/>
    <input type="submit" value="登录"/><br/>
</form>
</body>
</html>
```
修改配置参数：loginPage,放开/showLogin的权限

```java
http.formLogin()
    // 当发现/login时认为是登录，必须和表单提交的地址一样，去执行UserDetailsServiceImpl
    .loginProcessingUrl("/login")
    // 自定义登录页面
    .loginPage("/showLogin")
    // 登录成功后跳转页面，必须是post请求 successForwardUrl只能跳转系统内的地址，因为是转发请求
     .successForwardUrl("/toMain")
    //successHandler可以自定义跳转逻辑
    // 登录失败后跳转页面，必须是post请求
    .failureForwardUrl("/toError")
    // 自定义用户名参数名(当前端页面登录页面表单里的用户名不是username的时候需要定义)
    .usernameParameter("username111")
    .passwordParameter("password111");

// 授权认证
http.authorizeRequests()
    // login.html不需要被认证
    .antMatchers("/showLogin").permitAll()
    // error.html不需要被认证
    .antMatchers("/error.html").permitAll()
    .regexMatchers(".+[.]png").permitAll()
    .anyRequest().authenticated();
```
