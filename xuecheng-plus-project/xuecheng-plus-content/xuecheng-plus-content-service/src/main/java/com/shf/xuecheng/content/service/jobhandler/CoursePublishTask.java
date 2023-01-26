package com.shf.xuecheng.content.service.jobhandler;

import com.shf.xuecheng.base.exception.XueChengPlusException;
import com.shf.xuecheng.content.service.CoursePublishService;
import com.shf.xuecheng.messagesdk.model.po.MqMessage;
import com.shf.xuecheng.messagesdk.service.MessageProcessAbstract;
import com.shf.xuecheng.messagesdk.service.MqMessageService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class CoursePublishTask extends MessageProcessAbstract {

    @Autowired
    private CoursePublishService coursePublishService;

    /**
     * 课程发布消息类型
     */
    public static final String MESSAGE_TYPE = "course_publish";

    @XxlJob("CoursePublishJobHandler")
    public void coursePublishJobHandler() {
//        分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();

        log.debug("shardIndex="+shardIndex+",shardTotal="+shardTotal);
        //参数:分片序号、分片总数、消息类型、一次最多取到的任务数量、一次任务调度执行的超时时间
        process(shardIndex, shardTotal, MESSAGE_TYPE, 5, 60);
    }


    /**
     * 课程发布任务处理
     * @param mqMessage 执行任务内容
     * @return
     */
    @Override
    public boolean execute(MqMessage mqMessage) {
        //获取消息相关的业务信息
        String businessKey1 = mqMessage.getBusinessKey1();
        long courseId = Integer.parseInt(businessKey1);
        //课程静态化
        generateCourseHtml(mqMessage,courseId);
        //课程缓存
        saveCourseCache(mqMessage,courseId);
        //课程索引
        saveCourseIndex(mqMessage,courseId);

        return true;
    }

    /**
     * 生成课程静态化页面并上传至文件系统
     */
    public void generateCourseHtml(MqMessage mqMessage,long courseId){
        log.debug("开始进行课程静态化,课程id:{}",courseId);
        //消息id
        Long id = mqMessage.getId();
        //消息处理的service
        MqMessageService mqMessageService = this.getMqMessageService();
        //消息幂等性处理
        int stageOne = mqMessageService.getStageOne(id);
        if(stageOne == 1){
            log.debug("课程静态化已处理直接返回，课程id:{}",courseId);
            return ;
        }
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //保存第一阶段状态
        mqMessageService.completedStageOne(id);
        if(stageOne>0){
            log.debug("当前阶段是静态化课程信息任务已经完成不再处理,任务信息:{}",mqMessage);
            return ;
        }

//        生成静态化页面
        File file = coursePublishService.generateCourseHtml(courseId);
        if (file == null) {
            XueChengPlusException.cast("课程静态化异常");
        }

//        上传静态化页面
        coursePublishService.uploadCourseHtml(courseId, file);

//        保存第一阶段状态
        mqMessageService.completedStageOne(id);
    }

    /**
     * 将课程信息缓存至redis
     */
    public void saveCourseCache(MqMessage mqMessage,long courseId){
        log.debug("将课程信息缓存至redis,课程id:{}",courseId);
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 保存课程索引信息
     */
    public void saveCourseIndex(MqMessage mqMessage,long courseId){
        log.debug("保存课程索引信息,课程id:{}",courseId);

//        消息id
        Long id = mqMessage.getId();

//        消息处理的service
        MqMessageService mqMessageService = getMqMessageService();

//        消息幂等性处理
        int stageTwo = mqMessageService.getStageTwo(id);
        if (stageTwo == 2) {
            log.debug("课程索引已处理直接返回，课程id:{}",courseId);
            return ;
        }

        Boolean result = coursePublishService.saveCourseIndex(courseId);
        if (result) {
//            保存第二阶段状态
            mqMessageService.completedStageTwo(id);
        }
    }
}
