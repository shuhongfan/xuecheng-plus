package com.shf.xuecheng.learning.service;

import com.shf.xuecheng.base.model.PageResult;
import com.shf.xuecheng.learning.model.dto.MyCourseTableItemDto;
import com.shf.xuecheng.learning.model.dto.MyCourseTableParams;
import com.shf.xuecheng.learning.model.dto.XcChooseCourseDto;
import com.shf.xuecheng.learning.model.dto.XcCourseTablesDto;

/**
 * @description 我的课程表service接口
 * @author Mr.M
 * @date 2022/10/2 16:07
 * @version 1.0
 */
public interface MyCourseTablesService {

    /**
     * 添加选课
     * @param userId
     * @param courseId
     * @return
     */
    public XcChooseCourseDto addChooseCourse(String userId, Long courseId);

    /**
     * @description 判断学习资格
     * @param userId
     * @param courseId
     * @return XcCourseTablesDto 学习资格状态 [{"code":"702001","desc":"正常学习"},{"code":"702002","desc":"没有选课或选课后没有支付"},{"code":"702003","desc":"已过期需要申请续期或重新支付"}]
     * @author Mr.M
     * @date 2022/10/3 7:37
     */
    public XcCourseTablesDto getLearningStatus(String userId, Long courseId);


    /**
     * 添加选课
     * @param choosecourseId
     * @return
     */
    public boolean saveChooseCourseStatus(String choosecourseId);

    /**
     * 我的课表
     * @param params
     * @return
     */
    public PageResult<MyCourseTableItemDto> mycourestabls(MyCourseTableParams params);

}
