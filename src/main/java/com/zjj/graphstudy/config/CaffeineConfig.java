package com.zjj.graphstudy.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author zengJiaJun
 * @crateTime 2024年06月16日 20:54
 * @version 1.0
 */
@Configuration
@EnableCaching
public class CaffeineConfig {

    @Bean
    public Caffeine<Object, Object> caffeine() {
//        return Caffeine.newBuilder()
//                // 设置最后一次写入或访问后经过固定时间过期
//                .expireAfterWrite(30, TimeUnit.SECONDS)
//                // 初始的缓存空间大小
//                .initialCapacity(100)
//                // 缓存的最大条数
//                .maximumSize(1000)
//                .build();
        return Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(30, TimeUnit.SECONDS);
    }

}
