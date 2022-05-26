local key = KEYS[1]
local requestId = KEYS[2]

local value = redis.call('get', key);
-- 当锁是指定线程抢的时候才可以取消锁
if value == requestId then
    redis.call('del', key)
    return 1;
end

return -1;
