package com.shf.xuecheng.content.feignclient;

import com.shf.xuecheng.content.feignclient.model.CourseIndex;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 搜索服务远程接口
 */
@FeignClient(
        value = "search",
        fallbackFactory = SearchServiceClientFallbackFactory.class
)
public interface SearchServiceClient {

    /**
     * 添加索引
     * @param courseIndex
     * @return
     */
    @PostMapping("/search/index/course")
    public Boolean add(@RequestBody CourseIndex courseIndex);

}
