package com.shf.xuecheng.ucenter.service;

import com.shf.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.shf.xuecheng.ucenter.model.dto.XcUserExt;

public interface AuthService {
    /**
     * 认证方法
     * @param authParamsDto
     * @return
     */
    XcUserExt execute(AuthParamsDto authParamsDto);
}
