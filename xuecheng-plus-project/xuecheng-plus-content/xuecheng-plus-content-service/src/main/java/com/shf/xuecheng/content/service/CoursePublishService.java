package com.shf.xuecheng.content.service;

import com.shf.xuecheng.content.model.dto.CoursePreviewDto;

import java.io.File;

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

    /**
     * @description 课程静态化
     * @param courseId  课程id
     * @return File 静态化文件
     */
    public File generateCourseHtml(Long courseId);

    /**
     * @description 上传课程静态化页面
     * @param file  静态化文件
     * @return void
     */
    public void uploadCourseHtml(Long courseId,File file);

    /**
     * 保存课程索引
     * @param courseId
     * @return
     */
    public Boolean saveCourseIndex(Long courseId);
}
