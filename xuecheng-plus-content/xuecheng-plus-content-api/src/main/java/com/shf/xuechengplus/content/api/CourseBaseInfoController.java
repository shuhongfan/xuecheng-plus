package com.shf.xuechengplus.content.api;

import com.shf.xuechengplus.base.model.PageParams;
import com.shf.xuechengplus.base.model.PageResult;
import com.shf.xuechengplus.content.model.dto.QueryCourseParamsDto;
import com.shf.xuechengplus.content.model.po.CourseBase;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CourseBaseInfoController {

    @PostMapping("/course/list")
    public PageResult<CourseBase> list(
            PageParams params,
            @RequestBody QueryCourseParamsDto queryCourseParamsDto){

        return null;
    }
}
