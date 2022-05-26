package com.sixsixsix516.seckill.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * @author SUN
 * @date 2022/5/4
 */
@Configuration
public class MyBatisPlusConfig {

    /**
     * 自动赋值配置
     */
    @Bean
    public MetaObjectHandler metaObjectHandlerConfig() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                setFieldValByName("createTime", LocalDateTime.now(), metaObject);
                setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
            }
        };
    }
}
