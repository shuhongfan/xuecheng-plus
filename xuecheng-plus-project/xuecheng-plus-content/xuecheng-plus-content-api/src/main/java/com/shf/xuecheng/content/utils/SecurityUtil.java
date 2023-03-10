package com.shf.xuecheng.content.utils;

import com.alibaba.fastjson.JSON;
import com.shf.xuecheng.base.exception.XueChengPlusException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Slf4j
public class SecurityUtil {

    /**
     * 取出当前用户身份
     * @return
     */
    public static XcUser getUser() {
//        拿到JWT中的用户身份
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof String) {
            String jsonString = (String) principal;
            XcUser xcUser = null;
            try {
                xcUser = JSON.parseObject(jsonString, XcUser.class);
            } catch (Exception e) {
                log.debug("解析JWT中的用户身份无法转换成XcUser对象:{}",jsonString);
                XueChengPlusException.cast("解析JWT中的用户身份无法转换成XcUser对象:"+jsonString);
            }
            return xcUser;
        }
        return null;
    }

    @Data
    public static class XcUser implements Serializable {

        private static final long serialVersionUID = 1L;

        private String id;

        private String username;

        private String password;

        private String salt;

        private String name;
        private String nickname;
        private String wxUnionid;
        private String companyId;
        /**
         * 头像
         */
        private String userpic;

        private String utype;

        private LocalDateTime birthday;

        private String sex;

        private String email;

        private String cellphone;

        private String qq;

        /**
         * 用户状态
         */
        private String status;

        private LocalDateTime createTime;

        private LocalDateTime updateTime;
    }
}
