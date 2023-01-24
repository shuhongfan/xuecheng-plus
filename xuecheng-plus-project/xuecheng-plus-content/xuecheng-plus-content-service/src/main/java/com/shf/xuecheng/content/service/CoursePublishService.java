package com.shf.xuecheng.content.service;

import com.shf.xuecheng.content.model.dto.CoursePreviewDto;

/**
 * 课程发布
 */
public interface CoursePublishService {

    /**
     * 获取课程预览信息
     * @param courseId
     * @return
     */
    public CoursePreviewDto getCoursePreviewInfo(Long courseId);

    /**
     * 课程提交审核
     * @param companyId
     * @param courseId
     */
    public void commitAudit(Long companyId,Long courseId);

    /**
     * 课程发布接口
     * @param companyId
     * @param courseId
     */
    public void publish(Long companyId,Long courseId);

    /**
     * 保存消息表记录
     * @param courseId
     */
    public void saveCoursePublishMessage(Long courseId);

    /**
     * 保存课程发布信息
     * @param courseId
     */
    public void saveCoursePublish(Long courseId);
}
