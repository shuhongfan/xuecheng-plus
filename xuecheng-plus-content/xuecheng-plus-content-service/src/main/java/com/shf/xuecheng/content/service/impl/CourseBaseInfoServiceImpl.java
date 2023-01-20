package com.shf.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shf.xuecheng.base.exception.XueChengPlusException;
import com.shf.xuecheng.base.model.PageParams;
import com.shf.xuecheng.base.model.PageResult;
import com.shf.xuecheng.content.mapper.CourseBaseMapper;
import com.shf.xuecheng.content.mapper.CourseCategoryMapper;
import com.shf.xuecheng.content.mapper.CourseMarketMapper;
import com.shf.xuecheng.content.model.dto.AddCourseDto;
import com.shf.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.shf.xuecheng.content.model.dto.EditCourseDto;
import com.shf.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.shf.xuecheng.content.model.po.CourseBase;
import com.shf.xuecheng.content.model.po.CourseCategory;
import com.shf.xuecheng.content.model.po.CourseMarket;
import com.shf.xuecheng.content.service.CourseBaseInfoService;
import com.shf.xuecheng.content.service.CourseMarketService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {

    @Autowired
    private CourseBaseMapper courseBaseMapper;

    @Autowired
    private CourseMarketMapper courseMarketMapper;

    @Autowired
    private CourseCategoryMapper courseCategoryMapper;

    @Autowired
    private CourseMarketService courseMarketService;

    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams params, QueryCourseParamsDto queryCourseParamsDto) {
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();

//        拼接查询条件
//        根据课程名称模糊查询
        queryWrapper.like(
                StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()),
                CourseBase::getName,
                queryCourseParamsDto.getCourseName());

//        根据课审核状态
        queryWrapper.eq(
                StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()),
                CourseBase::getAuditStatus,
                queryCourseParamsDto.getAuditStatus());

//        根据课程发布状态
        queryWrapper.eq(
                StringUtils.isNotEmpty(queryCourseParamsDto.getPublishStatus()),
                CourseBase::getStatus,
                queryCourseParamsDto.getPublishStatus());

//        分页参数
        Page<CourseBase> page = new Page<>(params.getPageNo(), params.getPageSize());

//        分页查询
        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, queryWrapper);

//        数据
        List<CourseBase> items = pageResult.getRecords();
//        总记录数
        long total = pageResult.getTotal();

//        准备返回数据
        PageResult<CourseBase> courseBasePageResult = new PageResult<>(items, total, params.getPageNo(), params.getPageSize());
        return courseBasePageResult;
    }

    /**
     * 新增课程
     * @param companyId 培训机构id
     * @param addCourseDto 新增课程的信息
     * @return 课程基本信息 +  营销信息
     */
    @Override
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto) {
//        对参数进行合法性的校验
        //校验数据库合法性
//        if(StringUtils.isBlank(addCourseDto.getName())){
//            //抛出异常
////            throw  new RuntimeException("课程名称为空");
//            XueChengPlusException.cast("课程名称为空");
////            XueChengPlusException.cast(CommonError.PARAMS_ERROR);
//        }
//
//        if (StringUtils.isBlank(addCourseDto.getMt())) {
//            XueChengPlusException.cast("课程分类为空");
//        }
//
//        if (StringUtils.isBlank(addCourseDto.getSt())) {
//            XueChengPlusException.cast("课程分类为空");
//        }
//
//        if (StringUtils.isBlank(addCourseDto.getGrade())) {
//            XueChengPlusException.cast("课程等级为空");
//        }
//
//        if (StringUtils.isBlank(addCourseDto.getTeachmode())) {
//            XueChengPlusException.cast("教育模式为空");
//        }
//
//        if (StringUtils.isBlank(addCourseDto.getUsers())) {
//            XueChengPlusException.cast("适应人群为空");
//        }
//
//        if (StringUtils.isBlank(addCourseDto.getCharge())) {
//            XueChengPlusException.cast("收费规则为空");
//        }

//        对数据进行封装，调用mapper进行数据持久化
        CourseBase courseBase = new CourseBase();

//        将dto中CourseBase属性名一样的属性值拷贝到courseBase
        BeanUtils.copyProperties(addCourseDto, courseBase);

        //设置机构id
        courseBase.setCompanyId(companyId);
        //创建时间
        courseBase.setCreateDate(LocalDateTime.now());
        //审核状态默认未提交
        courseBase.setAuditStatus("202002");
        //发布状态默认为未发布
        courseBase.setStatus("203001");

//        课程表插入一条记录
        int insert = courseBaseMapper.insert(courseBase);

//        获取课程id
        Long courseId = courseBase.getId();

//        将dto中courseMarket属性名称一样的属性值拷贝到courseMarket
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(addCourseDto, courseMarket);
        courseMarket.setId(courseId);

//        校验如果课程为收费，价格必须输入
//        String charge = addCourseDto.getCharge();
//        if (charge.equals("201001")) {
//            if (courseMarket.getPrice() == null||courseMarket.getPrice().floatValue()<=0) {
//                XueChengPlusException.cast("课程为收费但是价格为空");
//            }
//        }

//        向营销表插入一条记录
//        int insert1 = courseMarketMapper.insert(courseMarket);
        int insert1 = saveCourseMarket(courseMarket);

        if (insert <= 0 || insert1 <= 0) {
            XueChengPlusException.cast("添加课程失败");
        }

//        组装要返回的数据,调用根据课程id查询课程的基本和营销信息接口
        CourseBaseInfoDto courseBaseInfo = getCourseBaseInfo(courseId);
        return courseBaseInfo;
    }

    /**
     * 根据课程id查询课程的基本和营销信息
     * @param courseId
     * @return
     */
    @Override
    public CourseBaseInfoDto getCourseBaseInfo(Long courseId) {
//        基本信息
        CourseBase courseBase = courseBaseMapper.selectById(courseId);

//        营销信息
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);

        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase,courseBaseInfoDto);
        BeanUtils.copyProperties(courseMarket,courseBaseInfoDto);

//        根据课程分类的id查询分类的名称
        String mt = courseBase.getMt();
        String st = courseBase.getSt();

        CourseCategory mtCourseCategory = courseCategoryMapper.selectById(mt);
        CourseCategory stCourseCategory = courseCategoryMapper.selectById(st);

//        mt分类名称
        if (mtCourseCategory != null) {
            String mtName = mtCourseCategory.getName();
            courseBaseInfoDto.setMtName(mtName);
        }

//        st分类名称
        if (stCourseCategory != null) {
            String stName = stCourseCategory.getName();
            courseBaseInfoDto.setStName(stName);
        }

        return courseBaseInfoDto;
    }

    /**
     * 修改课程
     * @param companyId
     * @param editCourseDto
     * @return
     */
    @Override
    public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto editCourseDto) {
//        校验课程id是否合法
        Long id = editCourseDto.getId();
        CourseBase courseBase = courseBaseMapper.selectById(id);
        if (courseBase == null) {
            XueChengPlusException.cast("课程不存在");
        }

//        校验本机构只能修改本机构的课程
        if (!courseBase.getCompanyId().equals(companyId)) {
            XueChengPlusException.cast("本机构只能修改本机构的课程");
        }

//        封装基本信息的数据
        BeanUtils.copyProperties(editCourseDto,courseBase);
//        设置更新时间
        courseBase.setChangeDate(LocalDateTime.now());

//        封装营销信息的数据
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(editCourseDto, courseMarket);

        //        校验如果课程为收费，价格必须输入
//        String charge = editCourseDto.getCharge();
//        if (charge.equals("201001")) {
//            if (courseMarket.getPrice() == null||courseMarket.getPrice().floatValue()<=0) {
//                XueChengPlusException.cast("课程为收费但是价格为空");
//            }
//        }

//        请求数据库
//        对营销表有则更新，没有则添加
//        boolean b = courseMarketService.saveOrUpdate(courseMarket);
        saveCourseMarket(courseMarket);

//        查询课程信息
        CourseBaseInfoDto courseBaseInfo = getCourseBaseInfo(id);
        return courseBaseInfo;
    }

    //    抽取对营销信息的保存
    private int saveCourseMarket(CourseMarket courseMarket) {
        //        校验如果课程为收费，价格必须输入
        String charge = courseMarket.getCharge();
        if (StringUtils.isBlank(charge)) {
            XueChengPlusException.cast("收费规则没有选择");
        }

        if (charge.equals("201001")) {
            if (courseMarket.getPrice() == null||courseMarket.getPrice().floatValue()<=0) {
                XueChengPlusException.cast("课程为收费但是价格为空");
            }
        }

        boolean b = courseMarketService.saveOrUpdate(courseMarket);
        return b?1:0;
    }
}
