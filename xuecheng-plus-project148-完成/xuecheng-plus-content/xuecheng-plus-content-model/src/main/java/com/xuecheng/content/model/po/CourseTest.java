package com.xuecheng.content.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @description TODO
 * @author Mr.M
 * @date 2022/10/23 10:08
 * @version 1.0
 */
@Data
@TableName("course_test")
public class CourseTest implements Serializable {

 private Long id;
 private String name;
 private Float price;
 private String qq;
 private String wechat;
 private String phone;
 private String description;
 private LocalDateTime createdate;

}
