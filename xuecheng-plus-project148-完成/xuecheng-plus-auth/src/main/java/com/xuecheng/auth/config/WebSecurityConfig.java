package com.xuecheng.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * @author Mr.M
 * @version 1.0
 * @description 安全管理配置
 * @date 2022/9/26 20:53
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true,prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    DaoAuthenticationProviderCustom daoAuthenticationProviderCustom;

    //使用自己定义DaoAuthenticationProviderCustom来代替框架的DaoAuthenticationProvider
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProviderCustom);
    }
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    //配置用户信息服务
//    @Bean
//    public UserDetailsService userDetailsService() {
//        //这里配置用户信息,这里暂时使用这种方式将用户存储在内存中
//        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
//        //在内存创建zhangsan,密码123,分配权限p1
//        manager.createUser(User.withUsername("zhangsan").password("123").authorities("p1").build());
//        //在内存创建lisi,密码456,分配权限p2
//        manager.createUser(User.withUsername("lisi").password("456").authorities("p2").build());
//        return manager;
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
//        //密码为明文方式
//        return NoOpPasswordEncoder.getInstance();
        return new BCryptPasswordEncoder();
    }

    //配置安全拦截机制
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/r/**").authenticated()//访问/r开始的请求需要认证通过
                .anyRequest().permitAll()//其它请求全部放行
                .and()
                .formLogin().successForwardUrl("/login-success");//登录成功跳转到/login-success

        http.logout().logoutUrl("/logout");//退出地址

    }


    public static void main(String[] args) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        /**
         $2a$10$1sp7I0OdYH3Azs/2lK8YYeuiaGZzOGshGT9j.IYArZftsGNsXqlma
         $2a$10$m983E2nmJ7ITlesbXzjbzO/M7HL2wP8EgpgX.pPACDm1wG38Lt.na
         $2a$10$rZvathrW98vVPenLhOnl0OMpUtRTdBkWJ45IkIsTebITS9AFgKqGK
         $2a$10$2gaMKWCRoKdc42E0jsq7b.munjzOSPOM4yr3GG9M6194E7dOH5LyS
         $2a$10$I/n93PIKpKL8m4O3AuT5kuZncZhfqV51bfx5sJrplnYoM7FimdboC
        */
//        for (int i = 0; i < 5; i++) {
//            String encode = passwordEncoder.encode("11111");
//            System.out.println(encode);
//        }

        boolean matches = passwordEncoder.matches("11111", "$2a$10$m983E2nmJ7ITlesbXzjbzO/M7HL2wP8EgpgX.pPACDm1wG38Lt.na");
        System.out.println(matches);
    }

}
