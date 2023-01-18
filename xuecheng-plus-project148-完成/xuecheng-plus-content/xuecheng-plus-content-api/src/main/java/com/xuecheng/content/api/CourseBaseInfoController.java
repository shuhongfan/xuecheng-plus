package com.xuecheng.content.api;

import com.xuecheng.base.exception.ValidationGroups;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseTestMapper;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseTest;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.service.CourseTestService;
import com.xuecheng.content.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @description 课程信息管理
 * @author Mr.M
 * @date 2022/10/7 16:22
 * @version 1.0
 */
@Api(value = "课程管理接口",tags = "课程管理接口")
@RestController
public class CourseBaseInfoController {

    @Autowired
    CourseBaseInfoService courseBaseInfoService;

    @ApiOperation("课程查询接口")
    @PreAuthorize("hasAuthority('course_find_list')")
    @PostMapping("/course/list")
  public PageResult<CourseBase> list(PageParams params, @RequestBody QueryCourseParamsDto queryCourseParamsDto){

        //当前登录用户
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        //机构id
        String companyId = user.getCompanyId();

        //调用service获取数据
        PageResult<CourseBase> courseBasePageResult = courseBaseInfoService.queryCourseBaseList(params, queryCourseParamsDto);

        return  courseBasePageResult;
    }

    @PostMapping("/course")
    public CourseBaseInfoDto createCourseBase(@RequestBody @Validated(ValidationGroups.Inster.class) AddCourseDto addCourseDto){

        Long companyId = 22L;
        return courseBaseInfoService.createCourseBase(companyId,addCourseDto);
    }

    @Autowired
    CourseTestMapper courseTestMapper;

    @Autowired
    CourseTestService courseTestService;

    @PostMapping("/coursetest")
    public CourseTest addCourseTest(@RequestBody CourseTest courseTest){

//        int insert = courseTestMapper.insert(courseTest);
        CourseTest courseTest1 = courseTestService.addCourseTest(courseTest);
        return courseTest1;
    }


    @GetMapping("/course/{courseId}")
    public CourseBaseInfoDto getCourseBaseById(@PathVariable Long courseId){

//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        System.out.println(user);
        return courseBaseInfoService.getCourseBaseInfo(courseId);
    }






    @PutMapping("/course")
    public CourseBaseInfoDto modifyCourseBase(@RequestBody EditCourseDto dto){

        Long companyId =1232141425L;
        return courseBaseInfoService.updateCourseBase(companyId,dto);
    }


    //测试分级校验用
//    @PostMapping("/course2")
//    public CourseBaseInfoDto createCourseBase2(@RequestBody @Validated(ValidationGroups.Update.class) AddCourseDto addCourseDto){
//
//        Long companyId = 22L;
//        return courseBaseInfoService.createCourseBase(companyId,addCourseDto);
//    }



}
