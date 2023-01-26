package com.shf.xuecheng.content.api;

import com.shf.xuecheng.base.exception.ValidationGroups;
import com.shf.xuecheng.base.model.PageParams;
import com.shf.xuecheng.base.model.PageResult;
import com.shf.xuecheng.content.model.dto.AddCourseDto;
import com.shf.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.shf.xuecheng.content.model.dto.EditCourseDto;
import com.shf.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.shf.xuecheng.content.service.CourseBaseInfoService;
import com.shf.xuecheng.content.model.po.CourseBase;
import com.shf.xuecheng.content.utils.SecurityUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
public class CourseBaseInfoController {

    @Autowired
    private CourseBaseInfoService courseBaseInfoService;

    @ApiOperation("课程查询接口")
    @PostMapping("/course/list")
    public PageResult<CourseBase> list(
            PageParams params,
            @RequestBody QueryCourseParamsDto queryCourseParamsDto) {
        PageResult<CourseBase> courseBasePageResult = courseBaseInfoService.queryCourseBaseList(params, queryCourseParamsDto);
        return courseBasePageResult;
    }

    @ApiOperation("新增课程")
    @PostMapping("/course")
    public CourseBaseInfoDto createCourseBase(
            @RequestBody
            @Validated(ValidationGroups.Inster.class) // JSR303 校验，并指定校验组
            AddCourseDto addCourseDto) {
//        获取当前用户所属培训机构id
        Long companyId = 22L;

//        调用Service
        CourseBaseInfoDto courseBase = courseBaseInfoService.createCourseBase(companyId, addCourseDto);

        return courseBase;
    }

    @ApiOperation("根据课程ID查询课程")
    @GetMapping("/course/{courseId}")
    public CourseBaseInfoDto getCourseBaseById(@PathVariable Long courseId) {
        //取出当前用户身份
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        System.out.println(principal);
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        System.out.println(user);

        return courseBaseInfoService.getCourseBaseInfo(courseId);
    }

    @ApiOperation("修改课程")
    @PutMapping("/course")
    public CourseBaseInfoDto modifyCourseBase(@RequestBody EditCourseDto editCourseDto) {
        //        获取当前用户所属培训机构id
        Long companyId = 1232141425L;
        return courseBaseInfoService.updateCourseBase(companyId,editCourseDto);
    }
}
