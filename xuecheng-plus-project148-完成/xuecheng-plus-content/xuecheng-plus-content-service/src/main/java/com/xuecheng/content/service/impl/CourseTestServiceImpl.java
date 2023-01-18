package com.xuecheng.content.service.impl;

import com.xuecheng.content.mapper.CourseTestMapper;
import com.xuecheng.content.model.po.CourseTest;
import com.xuecheng.content.service.CourseTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description TODO
 * @author Mr.M
 * @date 2022/10/23 10:25
 * @version 1.0
 */
 @Service
public class CourseTestServiceImpl implements CourseTestService {

  @Autowired
 CourseTestMapper courseTestMapper;

 @Override
 public CourseTest addCourseTest(CourseTest courseTest) {
  int insert = courseTestMapper.insert(courseTest);
  return courseTest;
 }
}
