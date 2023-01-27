package com.shf.xuecheng.learning.feignclient;

import com.shf.xuecheng.content.model.po.CoursePublish;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "content-api",fallbackFactory = ContentServiceClientFactory.class)
public interface ContentServiceClient {
    @GetMapping("/r/coursepublish/{courseId}")
    public CoursePublish getCoursepublish(@PathVariable("courseId") Long courseId);
}
