package com.shf.xuecheng.content.service;

import com.shf.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.shf.xuecheng.content.model.dto.SaveTeachplanDto;
import com.shf.xuecheng.content.model.dto.TeachplanDto;
import com.shf.xuecheng.content.model.po.TeachplanMedia;

import java.util.List;

public interface TeachplanService {

    /**
     * 课程计划查询
     * @param courseId
     * @return
     */
    public List<TeachplanDto> findTeachplayTree(Long courseId);

    /**
     * 课程计划的创建或修改
     * @param saveTeachplanDto
     */
    void saveTeachplan(SaveTeachplanDto saveTeachplanDto);

    /**
     * 获取最新排序号
     * @param courseId
     * @param parentId
     * @return
     */
    public int getTeachplanCount(Long courseId, Long parentId);

    /**
     * 课程计划删除
     * @param id
     */
    void deleteTeachplan(long id);


    /**
     * 课程计划下移
     * @param id
     */
    void moveDownTeachplan(long id);

    /**
     * 课程计划上移
     * @param id
     */
    void moveUpTeachplan(long id);

    /**
     * 课程计划和媒资信息绑定
     * @param bindTeachplanMediaDto
     * @return
     */
    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto);
}
