package com.shf.xuecheng.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shf.xuecheng.ucenter.feignclient.CheckCodeClient;
import com.shf.xuecheng.ucenter.mapper.XcUserMapper;
import com.shf.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.shf.xuecheng.ucenter.model.dto.XcUserExt;
import com.shf.xuecheng.ucenter.model.po.XcUser;
import com.shf.xuecheng.ucenter.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 账户和密码认证
 */
@Service("password_authservice")
@Slf4j
public class PasswordAuthServiceImpl implements AuthService {

    @Autowired
    private XcUserMapper xcUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CheckCodeClient checkCodeClient;

    /**
     * 实现账户和密码认证
     * @param authParamsDto
     * @return
     */
    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {
//        得到验证码
        String checkcode = authParamsDto.getCheckcode();
        String checkcodekey = authParamsDto.getCheckcodekey();
        if (StringUtils.isBlank(checkcodekey) || StringUtils.isBlank(checkcode)) {
            throw new RuntimeException("验证码为空");
        }

//        校验验证码，请求验证码服务进行校验
        Boolean result = checkCodeClient.verify(checkcodekey, checkcode);
        if (result == null || !result) {
            throw new RuntimeException("验证码错误");
        }


//        查询数据库
        String username = authParamsDto.getUsername();

//        从数据库查询用户信息
        LambdaQueryWrapper<XcUser> wrapper = new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, username);
        XcUser xcUser = xcUserMapper.selectOne(wrapper);
        if (xcUser == null) {
//            账户不存在
            throw new RuntimeException("账户不存在");
        }

//        对比密码
        String passwordDB = xcUser.getPassword();
        String password = authParamsDto.getPassword();
        boolean matches = passwordEncoder.matches(password, passwordDB);
        if (!matches) {
            throw new RuntimeException("账户或密码错误");
        }
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(xcUser, xcUserExt);
        return xcUserExt;
    }
}
