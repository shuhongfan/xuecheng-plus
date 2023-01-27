package com.shf.xuecheng.learning.feignclient;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 内容管理服务远程接口降级类
 */
@Slf4j
@Component
public class ContentServiceClientFactory implements FallbackFactory<ContentServiceClient> {
    @Override
    public ContentServiceClient create(Throwable throwable) {
        log.error("调用内容管理服务接口熔断:{}",throwable.getMessage());
        return null;
    }
}
