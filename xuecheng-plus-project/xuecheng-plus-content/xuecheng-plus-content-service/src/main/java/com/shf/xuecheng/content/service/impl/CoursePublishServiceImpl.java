package com.shf.xuecheng.content.service.impl;

import com.alibaba.fastjson.JSON;
import com.shf.xuecheng.base.exception.XueChengPlusException;
import com.shf.xuecheng.content.mapper.CourseBaseMapper;
import com.shf.xuecheng.content.mapper.CourseMarketMapper;
import com.shf.xuecheng.content.mapper.CoursePublishMapper;
import com.shf.xuecheng.content.mapper.CoursePublishPreMapper;
import com.shf.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.shf.xuecheng.content.model.dto.CoursePreviewDto;
import com.shf.xuecheng.content.model.dto.TeachplanDto;
import com.shf.xuecheng.content.model.po.CourseBase;
import com.shf.xuecheng.content.model.po.CourseMarket;
import com.shf.xuecheng.content.model.po.CoursePublish;
import com.shf.xuecheng.content.model.po.CoursePublishPre;
import com.shf.xuecheng.content.service.CourseBaseInfoService;
import com.shf.xuecheng.content.service.CoursePublishService;
import com.shf.xuecheng.content.service.TeachplanService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CoursePublishServiceImpl implements CoursePublishService {

    @Autowired
    private CourseBaseInfoService courseBaseInfoService;

    @Autowired
    private TeachplanService teachplanService;

    @Autowired
    private CourseBaseMapper courseBaseMapper;

    @Autowired
    private CourseMarketMapper courseMarketMapper;

    @Autowired
    private CoursePublishMapper coursePublishMapper;

    @Autowired
    private CoursePublishPreMapper coursePublishPreMapper;

    /**
     * 获取课程预览信息
     * @param courseId
     * @return
     */
    @Override
    public CoursePreviewDto getCoursePreviewInfo(Long courseId) {
//        基本信息、营销信息
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);

//        教学计划
        List<TeachplanDto> teachplayTree = teachplanService.findTeachplayTree(courseId);

        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        coursePreviewDto.setCourseBase(courseBaseInfo);
        coursePreviewDto.setTeachplans(teachplayTree);

        return coursePreviewDto;
    }

    /**
     * 课程提交审核
     * @param companyId
     * @param courseId
     */
    @Override
    public void commitAudit(Long companyId,Long courseId) {
//        约束校验
        CourseBase courseBase = courseBaseMapper.selectById(courseId);

//        课程审核状态
        String auditStatus = courseBase.getAuditStatus();

//        当前审核状态已提交不允许再次提交
        if ("202003".equals(auditStatus)) {
            XueChengPlusException.cast("当前为深圳状态,审核完成后可以再次提交");
        }

//        本机构只允许提交本机构的课程
        if (!courseBase.getCompanyId().equals(companyId)) {
            XueChengPlusException.cast("不允许提交其他机构的课程！");
        }

//        课程图片是否填写
        if (StringUtils.isEmpty(courseBase.getPic())) {
            XueChengPlusException.cast("提交失败，请上传课程图片");
        }

//        添加课程预发布记录
        CoursePublishPre coursePublishPre = new CoursePublishPre();
        
//        课程基本信息和部分营销信息
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
        BeanUtils.copyProperties(courseBaseInfo, coursePublishPre);

//        课程营销信息
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        
//        转为JSON
        String courseMarketJson = JSON.toJSONString(courseMarket);
        
//        将课程营销信息JSON数据放入课程预发布表
        coursePublishPre.setMarket(courseMarketJson);
        
//        查询课程计划信息
        List<TeachplanDto> teachplayTree = teachplanService.findTeachplayTree(courseId);
        if (teachplayTree.size() <= 0) {
            XueChengPlusException.cast("提交失败，还没有添加课程计划");
        }
        
//        转JSON
        String teachplanTreeString = JSON.toJSONString(teachplayTree);
        coursePublishPre.setTeachplan(teachplanTreeString);
        
//        设置预发布记录状态，已提交
        coursePublishPre.setStatus("202003");
        
//        教学机构ID
        coursePublishPre.setCompanyId(companyId);
        
//        提交时间
        coursePublishPre.setCreateDate(LocalDateTime.now());
        CoursePublish coursePublishPreUpdate = coursePublishMapper.selectById(courseId);

        if (coursePublishPreUpdate == null) {
//            添加课程预发布记录
            coursePublishPreMapper.insert(coursePublishPre);
        } else {
            coursePublishPreMapper.updateById(coursePublishPre);
        }

//        更新课程基本表的审核状态
        courseBase.setAuditStatus("202003");
        courseBaseMapper.updateById(courseBase);
    }

    /**
     * 课程发布接口
     * @param companyId
     * @param courseId
     */
    @Override
    @Transactional
    public void publish(Long companyId, Long courseId) {
//        查询课程预发布表
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);

        if (coursePublishPre == null) {
            XueChengPlusException.cast("请先提交课程审核，审核通过后才可以发布");
        }

//        本机构只允许提交本机构的课程
        if (!coursePublishPre.getCompanyId().equals(companyId)) {
            XueChengPlusException.cast("不允许提交其他机构课程！");
        }

//        课程审核状态
        String auditStatus = coursePublishPre.getStatus();

//        审核通过后可以发布
        if (!"202004".equals(auditStatus)) {
            XueChengPlusException.cast("操作失败,课程审核通过方可发布");
        }

        //保存课程发布信息
        saveCoursePublish(courseId);

        //保存消息表
        saveCoursePublishMessage(courseId);

        //删除课程预发布表对应记录
        coursePublishPreMapper.deleteById(courseId);
    }

    /**
     * 保存消息表记录
     * @param courseId
     */
    public void saveCoursePublishMessage(Long courseId) {

    }

    /**
     * 保存课程发布信息
     * @param courseId 
     */
    public void saveCoursePublish(Long courseId) {
//        整合课程发布信息
//        整合课程预发布表
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPre == null) {
            XueChengPlusException.cast("课程预发布数据为空");
        }

        CoursePublish coursePublish = new CoursePublish();
        BeanUtils.copyProperties(coursePublishPre, coursePublish);
        coursePublish.setStatus("203002");
        CoursePublish coursePublishUpdate = coursePublishMapper.selectById(courseId);
        if (coursePublishUpdate == null) {
            coursePublishMapper.insert(coursePublish);
        } else {
            coursePublishMapper.updateById(coursePublish);
        }

//        更新课程基本表的发布状态
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        courseBase.setStatus("203002");
        courseBaseMapper.updateById(courseBase);
    }
}