package com.shf.xuecheng.search.controller;

import com.shf.xuecheng.base.model.PageParams;
import com.shf.xuecheng.search.dto.SearchCourseParamDto;
import com.shf.xuecheng.search.dto.SearchPageResultDto;
import com.shf.xuecheng.search.po.CourseIndex;
import com.shf.xuecheng.search.service.CourseSearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description 课程搜索接口
 * @author Mr.M
 * @date 2022/9/24 22:31
 * @version 1.0
 */
@Api(value = "课程搜索接口",tags = "课程搜索接口")
@RestController
@RequestMapping("/course")
public class CourseSearchController {

 @Autowired
 CourseSearchService courseSearchService;

 @ApiOperation("课程搜索列表")
 @GetMapping("/list")
 public SearchPageResultDto<CourseIndex> list(PageParams pageParams, SearchCourseParamDto searchCourseParamDto){
    return courseSearchService.queryCoursePubIndex(pageParams,searchCourseParamDto);
  }
}
