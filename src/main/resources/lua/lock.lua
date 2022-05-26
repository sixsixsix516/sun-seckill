local SUCCESS = 1;
local FAILED = -1;

-- 键值对key
local key = KEYS[1]
-- 身份标识
local requestId = KEYS[2]
local ttl = tonumber(KEYS[3])
local result = redis.call("setnx", key, requestId);

if result == SUCCESS then
    -- 设置成功、指定过期时间（毫秒单位）
    redis.call("pexpire", key, ttl);
else
    -- 设置失败
    result = FAILED;
    local value = redis.call('get', key)
    if value == requestId then
        -- 如果是同一个线程获取锁，那么算成功，可重入
        result = SUCCESS;
        redis.call('pexpire', key, ttl)
    end
end

-- 如果获取锁成功，就返回1
return result;

