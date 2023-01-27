package com.shf.xuecheng.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shf.xuecheng.ucenter.mapper.XcUserMapper;
import com.shf.xuecheng.ucenter.mapper.XcUserRoleMapper;
import com.shf.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.shf.xuecheng.ucenter.model.dto.XcUserExt;
import com.shf.xuecheng.ucenter.model.po.XcUser;
import com.shf.xuecheng.ucenter.model.po.XcUserRole;
import com.shf.xuecheng.ucenter.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service("wx_authservice")
@Slf4j
public class WxAuthServiceImpl implements AuthService {

    @Autowired
    private XcUserMapper xcUserMapper;

    @Autowired
    private XcUserRoleMapper xcUserRoleMapper;

    @Autowired
    private WxAuthServiceImpl currentProxy;

    @Value("${weixin.appid}")
    String appid;

    @Value("${weixin.secret}")
    String secret;

    @Autowired
    private RestTemplate restTemplate;

    public XcUser wxAuth(String code) {

        //获取access_token
        Map<String, String> accessToken = getAccess_token(code);
        System.out.println("accessToken=" + accessToken);

//        得到令牌
        String access_token = accessToken.get("access_token");

//        得到openid
        String openid = accessToken.get("openid");

        //获取用户信息
        Map<String, String> userinfo = getUserinfo(access_token, openid);
        System.out.println(userinfo);

        //添加用户到数据库
        XcUser xcUser = currentProxy.addWxUser(userinfo);
        return xcUser;
    }

    /**
     * 向数据库保存用户信息，如果用户不存在将其保存在数据库。
     * @param userInfo_map
     * @return
     */
    @Transactional
    public XcUser addWxUser(Map userInfo_map){
//        向取出unionId
        String unionid = (String) userInfo_map.get("unionid");

//        根据unionid查询数据库
        LambdaQueryWrapper<XcUser> wrapper = new LambdaQueryWrapper<XcUser>().eq(XcUser::getWxUnionid, unionid);
        XcUser xcUser = xcUserMapper.selectOne(wrapper);
        String userId = UUID.randomUUID().toString();
        xcUser = new XcUser();
        xcUser.setId(userId);
        xcUser.setWxUnionid(unionid);
        //记录从微信得到的昵称
        xcUser.setNickname(userInfo_map.get("nickname").toString());
        xcUser.setUserpic(userInfo_map.get("headimgurl").toString());
        xcUser.setName(userInfo_map.get("nickname").toString());
        xcUser.setUsername(unionid);
        xcUser.setPassword(unionid);
        xcUser.setUtype("101001");//学生类型
        xcUser.setStatus("1");//用户状态
        xcUser.setCreateTime(LocalDateTime.now());
        xcUserMapper.insert(xcUser);
        XcUserRole xcUserRole = new XcUserRole();
        xcUserRole.setId(UUID.randomUUID().toString());
        xcUserRole.setUserId(userId);
        xcUserRole.setRoleId("17");//学生角色
        xcUserRoleMapper.insert(xcUserRole);
        return xcUser;
    }

    /**
     * 微信认证方法
     * @param authParamsDto
     * @return
     */
    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {
        String username = authParamsDto.getUsername();
        XcUser xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, username));
        if (xcUser == null) {
            throw new RuntimeException("用户不存在");
        }
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(xcUser, xcUserExt);
        return xcUserExt;
    }

    /**
     * 获取access_token
     * 申请访问令牌,响应示例
     * {
     * "access_token":"ACCESS_TOKEN",
     * "expires_in":7200,
     * "refresh_token":"REFRESH_TOKEN",
     * "openid":"OPENID",
     * "scope":"SCOPE",
     * "unionid": "o6_bmasdasdsad6_2sgVt7hMZOPfL"
     * }
     */
    private Map<String, String> getAccess_token(String code) {
        String wxUrl_template = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
        String url = String.format(wxUrl_template, appid, secret, code);
//        请求微信获取令牌
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, null, String.class);
        System.out.println(response);

//        得到响应串
        String body = response.getBody();

//        将JSON转换成MAP
        Map map = JSON.parseObject(body, Map.class);
        return map;
    }

    /**
     * 获取用户信息，示例如下：
     * {
     * "openid":"OPENID",
     * "nickname":"NICKNAME",
     * "sex":1,
     * "province":"PROVINCE",
     * "city":"CITY",
     * "country":"COUNTRY",
     * "headimgurl": "https://thirdwx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/0",
     * "privilege":[
     * "PRIVILEGE1",
     * "PRIVILEGE2"
     * ],
     * "unionid": " o6_bmasdasdsad6_2sgVt7hMZOPfL"
     * }
     */
    private Map<String, String> getUserinfo(String access_token, String openid) {
//        请求文学查询用户信息
        String wxUrl_template = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s";
        String url = String.format(wxUrl_template, access_token, openid);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        String body = response.getBody();
//        将结果转换成MAP
        Map map = JSON.parseObject(body, Map.class);
        return map;
    }
}