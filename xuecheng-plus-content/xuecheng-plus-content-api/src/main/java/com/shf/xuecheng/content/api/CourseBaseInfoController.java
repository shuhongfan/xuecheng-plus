package com.shf.xuecheng.content.api;

import com.shf.xuecheng.base.model.PageParams;
import com.shf.xuecheng.base.model.PageResult;
import com.shf.xuecheng.content.model.dto.AddCourseDto;
import com.shf.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.shf.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.shf.xuecheng.content.service.CourseBaseInfoService;
import com.shf.xuecheng.content.model.po.CourseBase;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CourseBaseInfoController {

    @Autowired
    private CourseBaseInfoService courseBaseInfoService;

    @ApiOperation("课程查询接口")
    @PostMapping("/course/list")
    public PageResult<CourseBase> list(
            PageParams params,
            @RequestBody QueryCourseParamsDto queryCourseParamsDto){
        PageResult<CourseBase> courseBasePageResult = courseBaseInfoService.queryCourseBaseList(params, queryCourseParamsDto);
        return courseBasePageResult;
    }

    @ApiOperation("新增课程")
    @PostMapping("/course")
    public CourseBaseInfoDto createCourseBase(@RequestBody AddCourseDto addCourseDto) {
//        获取当前用户所属培训机构id
        Long companyId = 22L;

//        调用Service
        CourseBaseInfoDto courseBase = courseBaseInfoService.createCourseBase(companyId, addCourseDto);

        return courseBase;
    }

}
