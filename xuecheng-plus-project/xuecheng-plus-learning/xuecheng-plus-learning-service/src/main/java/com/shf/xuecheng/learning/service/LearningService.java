package com.shf.xuecheng.learning.service;


import com.shf.xuecheng.base.model.RestResponse;

/**
 * @description 学习过程管理service接口
 * @author Mr.M
 * @date 2022/10/2 16:07
 * @version 1.0
 */
public interface LearningService {

    /**
     * @description 获取教学视频
     * @param courseId 课程id
     * @param teachplanId 课程计划id
     * @param mediaId 视频文件id
     * @return com.xuecheng.base.model.RestResponse<java.lang.String>
     * @author Mr.M
     * @date 2022/10/5 9:08
    */
    public RestResponse<String> getVideo(String userId, Long courseId, Long teachplanId, String mediaId);


}
