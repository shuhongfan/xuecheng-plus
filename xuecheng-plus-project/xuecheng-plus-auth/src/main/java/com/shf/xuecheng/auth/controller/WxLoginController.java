package com.shf.xuecheng.auth.controller;

import com.shf.xuecheng.ucenter.model.po.XcUser;
import com.shf.xuecheng.ucenter.service.impl.WxAuthServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Slf4j
@Controller
public class WxLoginController {

    @Autowired
    private WxAuthServiceImpl wxAuthService;

    @RequestMapping("/wxLogin")
    public String wxLogin(String code, String state) {
        log.debug("微信扫码回调,code:{},state:{}",code,state);

//        拿授权码申请令牌，查询用户
        XcUser xcUser = wxAuthService.wxAuth(code);
        if (xcUser == null) {
            return "redirect:http://www.xuecheng-plus.com/error.html";
        } else {
            String username = xcUser.getUsername();
            return "redirect:http://www.xuecheng-plus.com/sign.html?username="+username+"&authType=wx";
        }

    }

}
