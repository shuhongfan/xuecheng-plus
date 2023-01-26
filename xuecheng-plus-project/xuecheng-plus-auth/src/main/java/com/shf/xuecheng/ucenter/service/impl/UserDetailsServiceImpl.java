package com.shf.xuecheng.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shf.xuecheng.ucenter.mapper.XcUserMapper;
import com.shf.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.shf.xuecheng.ucenter.model.dto.XcUserExt;
import com.shf.xuecheng.ucenter.model.po.XcUser;
import com.shf.xuecheng.ucenter.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private XcUserMapper xcUserMapper;

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 根据账号查询用户信息
     * @param s
     * @return
     */
    @Override
    public UserDetails loadUserByUsername(String s) {
        AuthParamsDto authParamsDto = null;
        try {
            authParamsDto = JSON.parseObject(s, AuthParamsDto.class);
        } catch (Exception e) {
            log.info("请求认证不符合项目要求:{}", s);
            throw new RuntimeException("认证请求数据格式不正确");
        }

//        认证方式
        String authType = authParamsDto.getAuthType();
//        从Spring容器中拿出具体的认证bean实例
        AuthService authService = applicationContext.getBean(authType + "_authservice", AuthService.class);
//        开始认证,认证成功拿到用户信息
        XcUserExt xcUserExt = authService.execute(authParamsDto);

        return getUserPrincipal(xcUserExt);
    }

    /**
     * @description 查询用户信息
     * @param user  用户id，主键
     * @return com.xuecheng.ucenter.model.po.XcUser 用户信息
     * @author Mr.M
     * @date 2022/9/29 12:19
     */
    public UserDetails getUserPrincipal(XcUserExt user) {
        //用户权限,如果不加报Cannot pass a null GrantedAuthority collection
        String[] authorities = {"p1"};
        String password = user.getPassword();
        //为了安全在令牌中不放密码
        user.setPassword(null);
        //将user对象转json
        String userString = JSON.toJSONString(user);
        //创建UserDetails对象
        UserDetails userDetails = User.withUsername(userString).password(password ).authorities(authorities).build();
        return userDetails;
    }
}
