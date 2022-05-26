package com.sixsixsix516.seckill.lock;

import com.sixsixsix516.seckill.utils.LuaUtil;
import com.sixsixsix516.seckill.utils.SpringApplicationContextUtil;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author SUN
 * @date 2022/5/6
 */
public class InnerLock {

    String key;
    String requestId;
    private final StringRedisTemplate redisTemplate;

    public InnerLock(String lockKey, String requestId) {
        this.key = lockKey;
        this.requestId = requestId;
        redisTemplate = SpringApplicationContextUtil.getBean(StringRedisTemplate.class);
    }


    /**
     * 抢夺锁
     */
    public boolean lock() {
        return lock(2000, TimeUnit.SECONDS);
    }

    public boolean lock(Integer time, TimeUnit unit) {
        if (key == null) {
            return false;
        }

        // lock
        Long res = redisTemplate.execute(LuaUtil.getLockScript(), List.of(key, requestId, String.valueOf(unit.toMillis(time))));
        System.out.println("获取锁Lua返回值：" + res);
        return res != null && res == 1;
    }


    public void unlock() {
        if (key == null || requestId == null) {
            return;
        }

        // unlock
        Long res = redisTemplate.execute(LuaUtil.getUnLockScript(), List.of(key, requestId));
        System.out.println("解锁Lua返回值：" + res);
    }




}
