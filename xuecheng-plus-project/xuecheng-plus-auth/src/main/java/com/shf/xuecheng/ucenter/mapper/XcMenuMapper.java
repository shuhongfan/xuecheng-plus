package com.shf.xuecheng.ucenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shf.xuecheng.ucenter.model.po.XcMenu;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author itcast
 */
public interface XcMenuMapper extends BaseMapper<XcMenu> {

    /**
     * 根据用户id获取用户权限
     * @param userId
     * @return
     */
    List<XcMenu> selectPermissionByUserId(@Param("userId") String userId);
}
