package com.sixsixsix516.seckill;

import com.sixsixsix516.seckill.utils.LuaUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@MapperScan("com.sixsixsix516.seckill.mapper")
@SpringBootApplication
public class SunSeckillApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(SunSeckillApplication.class, args);
    }

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void run(String... args) {
/*        LuaUtil.loadLuaScript();

        // 提前准备秒杀脚本
        String sha = Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().scriptLoad(LuaUtil.getSecKillScript().getScriptAsString().getBytes(StandardCharsets.UTF_8));
        assert sha != null;
        // 将sha存储起来
        redisTemplate.opsForValue().set("seckill:script:sha", sha);*/
    }
}
