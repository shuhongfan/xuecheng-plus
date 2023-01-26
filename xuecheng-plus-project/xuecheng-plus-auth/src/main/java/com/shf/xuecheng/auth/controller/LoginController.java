package com.shf.xuecheng.auth.controller;

import com.shf.xuecheng.ucenter.mapper.XcUserMapper;
import com.shf.xuecheng.ucenter.model.po.XcUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Mr.M
 * @version 1.0
 * @description 测试controller
 * @date 2022/9/27 17:25
 */
@Slf4j
@RestController
public class LoginController {

    @Autowired
    XcUserMapper userMapper;


    @RequestMapping("/login-success")
    public String loginSuccess() {

        return "登录成功";
    }


    @RequestMapping("/user/{id}")
    public XcUser getuser(@PathVariable("id") String id) {
        XcUser xcUser = userMapper.selectById(id);
        return xcUser;
    }

    @PreAuthorize("hasAnyAuthority('p1')")
    @RequestMapping("/r/r1")
    public String r1() {
        return "访问r1资源";
    }


    @PreAuthorize("hasAnyAuthority('p2')")
    @RequestMapping("/r/r2")
    public String r2() {
        return "访问r2资源";
    }



}
