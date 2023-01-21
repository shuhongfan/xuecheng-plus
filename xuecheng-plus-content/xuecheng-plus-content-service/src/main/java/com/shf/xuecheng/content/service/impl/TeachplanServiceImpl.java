package com.shf.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shf.xuecheng.base.exception.XueChengPlusException;
import com.shf.xuecheng.content.mapper.TeachplanMapper;
import com.shf.xuecheng.content.model.dto.SaveTeachplanDto;
import com.shf.xuecheng.content.model.dto.TeachplanDto;
import com.shf.xuecheng.content.model.po.Teachplan;
import com.shf.xuecheng.content.service.TeachplanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TeachplanServiceImpl implements TeachplanService {

    @Autowired
    private TeachplanMapper teachplanMapper;

    /**
     * 课程计划查询
     * @param courseId
     * @return
     */
    @Override
    public List<TeachplanDto> findTeachplayTree(Long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
    }

    /**
     * 课程计划的创建或修改
     * @param saveTeachplanDto
     */
    @Override
    public void saveTeachplan(SaveTeachplanDto saveTeachplanDto) {
        Long id = saveTeachplanDto.getId();
        Teachplan teachplan = teachplanMapper.selectById(id);
        if (teachplan == null) { // 新增
            teachplan = new Teachplan();
            BeanUtils.copyProperties(saveTeachplanDto, teachplan);
//            计算默认顺序
            int count = getTeachplanCount(saveTeachplanDto.getCourseId(), saveTeachplanDto.getParentid());

//            新课程计划的排序值
            teachplan.setOrderby(count + 1);

            teachplanMapper.insert(teachplan);
        } else { // 修改
            BeanUtils.copyProperties(saveTeachplanDto, teachplan);
            teachplanMapper.updateById(teachplan);
        }
    }

    /**
     * 获取最新排序号
     * @param courseId
     * @param parentId
     * @return
     */
    public int getTeachplanCount(Long courseId, Long parentId) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId, courseId);
        queryWrapper.eq(Teachplan::getParentid, parentId);
        return teachplanMapper.selectCount(queryWrapper);
    }

    /**
     * 课程计划删除
     * @param id
     */
    @Override
    public void deleteTeachplan(long id) {
        Teachplan teachplan = teachplanMapper.selectById(id);
        if (teachplan == null) {
//            课程计划不存在
            XueChengPlusException.cast("课程计划不存在");
        }

        LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teachplan::getParentid, id);
        List<Teachplan> teachplanList = teachplanMapper.selectList(wrapper);
        if (teachplanList.size()!=0) {
//            删除有子课程计划的计划
            XueChengPlusException.cast("课程计划还有子级信息，无法操作");
        }
//      删除没有子课程计划的计划
        int isDelete = teachplanMapper.deleteById(id);
        if (isDelete <= 0) {
            XueChengPlusException.cast("删除课程计划失败");
        }
    }

    /**
     * 课程计划下移
     * @param id
     */
    @Override
    public void moveDownTeachplan(long id) {
        Teachplan teachplan = teachplanMapper.selectById(id);
        if (teachplan == null) {
//            课程计划不存在
            XueChengPlusException.cast("课程计划不存在");
        }

        LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teachplan::getParentid, teachplan.getParentid());
        wrapper.eq(Teachplan::getCourseId, teachplan.getCourseId());
        wrapper.orderByAsc(Teachplan::getOrderby);
        List<Teachplan> teachplanList = teachplanMapper.selectList(wrapper);
//        获取最后一个元素
        Teachplan last = teachplanList.get(teachplanList.size() - 1);
        if (last.getId() == id) {
            XueChengPlusException.cast("课程计划已经属于最后一个，无法下移");
        } else {
            List<Teachplan> collect = teachplanList.stream().filter(item -> item.getId() == id).collect(Collectors.toList());
            Teachplan currentTeachplan = collect.get(0);
            int nextTeachplanOrderBy = currentTeachplan.getOrderby() + 1;
//            当前计划下移
            currentTeachplan.setOrderby(nextTeachplanOrderBy);
            teachplanMapper.updateById(currentTeachplan);

//            下一个计划上移
            LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Teachplan::getParentid, teachplan.getParentid());
            queryWrapper.ne(Teachplan::getId,currentTeachplan.getId());
            queryWrapper.eq(Teachplan::getOrderby, nextTeachplanOrderBy);
            queryWrapper.eq(Teachplan::getCourseId, teachplan.getCourseId());
            Teachplan updateTeachplan = teachplanMapper.selectOne(queryWrapper);
            updateTeachplan.setOrderby(teachplan.getOrderby());
            teachplanMapper.updateById(updateTeachplan);
        }
    }

    /**
     * 课程计划上移
     * @param id
     */
    @Override
    public void moveUpTeachplan(long id) {
        Teachplan teachplan = teachplanMapper.selectById(id);
        if (teachplan == null) {
//            课程计划不存在
            XueChengPlusException.cast("课程计划不存在");
        }

        LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teachplan::getParentid, teachplan.getParentid());
        wrapper.eq(Teachplan::getCourseId, teachplan.getCourseId());
        wrapper.orderByAsc(Teachplan::getOrderby);
        List<Teachplan> teachplanList = teachplanMapper.selectList(wrapper);
//        获取第一个元素
        Teachplan last = teachplanList.get(0);
        if (last.getId() == id) {
            XueChengPlusException.cast("课程计划已经属于第一个，无法上移");
        } else {
            List<Teachplan> collect = teachplanList.stream().filter(item -> item.getId() == id).collect(Collectors.toList());
            Teachplan currentTeachplan = collect.get(0);
            int preTeachplanOrderBy = currentTeachplan.getOrderby() - 1;
//            当前计划上移
            currentTeachplan.setOrderby(preTeachplanOrderBy);
            teachplanMapper.updateById(currentTeachplan);

//            下一个计划下移
            LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Teachplan::getParentid, teachplan.getParentid());
            queryWrapper.ne(Teachplan::getId,currentTeachplan.getId());
            queryWrapper.eq(Teachplan::getOrderby, preTeachplanOrderBy);
            queryWrapper.eq(Teachplan::getCourseId, teachplan.getCourseId());
            Teachplan updateTeachplan = teachplanMapper.selectOne(queryWrapper);
            updateTeachplan.setOrderby(teachplan.getOrderby());
            teachplanMapper.updateById(updateTeachplan);
        }
    }

}
