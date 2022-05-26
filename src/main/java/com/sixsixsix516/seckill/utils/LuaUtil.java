package com.sixsixsix516.seckill.utils;

import lombok.Getter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

/**
 * @author SUN
 * @date 2022/5/7
 */
public class LuaUtil {

    /**
     * 限流脚本
     */
    private static RedisScript<Long> rateLimiterScript;
    /**
     * 秒杀脚本
     */
    private static RedisScript<Long> secKillScript;
    /**
     * 获取锁脚本
     */
    private static RedisScript<Long> lockScript;
    /**
     * 解锁脚本
     */
    private static RedisScript<Long> unLockScript;

    public static RedisScript<Long> getRateLimiterScript() {
        return rateLimiterScript;
    }

    public static RedisScript<Long> getSecKillScript() {
        return secKillScript;
    }

    public static RedisScript<Long> getLockScript() {
        return lockScript;
    }

    public static RedisScript<Long> getUnLockScript() {
        return unLockScript;
    }

    /**
     * 加载Lua脚本
     */
    public static void loadLuaScript() {
        // 1.限流脚本
        if (rateLimiterScript == null) {
            DefaultRedisScript<Long> rateLimiterScript = new DefaultRedisScript<>();
            rateLimiterScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/rate_limiter.lua")));
            rateLimiterScript.setResultType(Long.class);
            LuaUtil.rateLimiterScript = rateLimiterScript;
        }

        // 2.秒杀脚本
        if (secKillScript == null) {
            DefaultRedisScript<Long> secKillScript = new DefaultRedisScript<>();
            secKillScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/seckill.lua")));
            secKillScript.setResultType(Long.class);
            LuaUtil.secKillScript = secKillScript;
        }

        if (lockScript == null) {
            DefaultRedisScript<Long> lockScript = new DefaultRedisScript<>();
            lockScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/lock.lua")));
            lockScript.setResultType(Long.class);
            LuaUtil.lockScript = lockScript;
        }
        if (unLockScript == null) {
            DefaultRedisScript<Long> unLockScript = new DefaultRedisScript<>();
            unLockScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/unlock.lua")));
            unLockScript.setResultType(Long.class);
            LuaUtil.unLockScript = unLockScript;
        }
    }
}
