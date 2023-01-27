package com.shf.xuecheng.content.service.impl;

import com.alibaba.fastjson.JSON;
import com.shf.xuecheng.base.exception.CommonError;
import com.shf.xuecheng.base.exception.XueChengPlusException;
import com.shf.xuecheng.content.config.MultipartSupportConfig;
import com.shf.xuecheng.content.feignclient.MediaServiceClient;
import com.shf.xuecheng.content.feignclient.SearchServiceClient;
import com.shf.xuecheng.content.feignclient.model.CourseIndex;
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
import com.shf.xuecheng.content.service.jobhandler.CoursePublishTask;
import com.shf.xuecheng.messagesdk.model.po.MqMessage;
import com.shf.xuecheng.messagesdk.service.MqMessageService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
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

    @Autowired
    private MqMessageService mqMessageService;

    @Autowired
    private MediaServiceClient mediaServiceClient;

    @Autowired
    private SearchServiceClient searchServiceClient;

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
        MqMessage mqMessage = mqMessageService.addMessage(
                CoursePublishTask.MESSAGE_TYPE,
                String.valueOf(courseId),
                null,
                null
        );
        if (mqMessage == null) {
            XueChengPlusException.cast(CommonError.UNKOWN_ERROR);
        }
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

    /**
     * 课程静态化
     * @param courseId  课程id
     * @return
     */
    @Override
    public File generateCourseHtml(Long courseId) {
        //静态化文件
        File htmlFile  = null;

        try {
            //配置freemarker
            Configuration configuration = new Configuration(Configuration.getVersion());

            //加载模板
            //选指定模板路径,classpath下templates下
            //得到classpath路径
            String classpath = this.getClass().getResource("/").getPath();
            configuration.setDirectoryForTemplateLoading(new File(classpath + "/templates/"));
            //设置字符编码
            configuration.setDefaultEncoding("utf-8");

            //指定模板文件名称
            Template template = configuration.getTemplate("course_template.ftl");

            //准备数据
            CoursePreviewDto coursePreviewInfo = this.getCoursePreviewInfo(courseId);

            Map<String, Object> map = new HashMap<>();
            map.put("model", coursePreviewInfo);

            //静态化
            //参数1：模板，参数2：数据模型
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
//            System.out.println(content);
            //将静态化内容输出到文件中
            InputStream inputStream = IOUtils.toInputStream(content);
            //创建静态化文件
            htmlFile = File.createTempFile("course",".html");
            log.debug("课程静态化，生成静态文件:{}",htmlFile.getAbsolutePath());
            //输出流
            FileOutputStream outputStream = new FileOutputStream(htmlFile);
            IOUtils.copy(inputStream, outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return htmlFile;
    }

    /**
     * 上传课程静态化页面
     * @param courseId
     * @param file  静态化文件
     */
    @Override
    public void uploadCourseHtml(Long courseId, File file) {
        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(file);
        String course = mediaServiceClient.uploadFile(multipartFile, "course", courseId+".html");
        if(course == null){
            XueChengPlusException.cast("远程调用媒资服务上传文件失败");
        }
    }

    /**
     * 保存课程索引
     * @param courseId
     * @return
     */
    @Override
    public Boolean saveCourseIndex(Long courseId) {
//        取出课程发布信息
        CoursePublish coursePublish = coursePublishMapper.selectById(courseId);

//        拷贝至课程索引对象
        CourseIndex courseIndex = new CourseIndex();
        BeanUtils.copyProperties(coursePublish, courseIndex);

//        远程调用搜索服务API提娜佳课程信息到索引
        Boolean add = searchServiceClient.add(courseIndex);
        if (!add) {
            XueChengPlusException.cast("添加索引失败");
        }
        return add;
    }

    /**
     * 查询课程发布信息
     * @param courseId
     * @return
     */
    @Override
    public CoursePublish getCoursePublish(Long courseId) {
        CoursePublish coursePublish = coursePublishMapper.selectById(courseId);
        return coursePublish;
    }
}
