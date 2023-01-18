package com.xuecheng;

import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseTestMapper;
import com.xuecheng.content.model.po.CourseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @description TODO
 * @author Mr.M
 * @date 2022/10/23 10:14
 * @version 1.0
 */
@SpringBootTest
public class CourseTestMapperTest {

 @Autowired//从容器中拿到courseTestMapper注入成员
 CourseTestMapper courseTestMapper;



 @Test
 public void testCourseTest(){
  CourseTest courseTest = new CourseTest();
  courseTest.setId(100L);
  courseTest.setName("课程名称");
  courseTestMapper.insert(courseTest);
 }
}
