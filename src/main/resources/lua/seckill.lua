--返回值说明
--1 排队成功
--2 秒杀库存没有找到
--3 人数超过限制
--4 库存不足
--5 排队过了
--6 秒杀过了
-- -2 Lua 方法不存在


---
--- 库存扣减
--- @param exposeKey '缓存键'
---
local function setToken(exposeKey, userId, token)
    local oldToken = redis.call("HGET", "seckill:queue:" .. exposeKey, userId);
    if oldToken then
        -- 排队过了
        return -5;
    end

    local stock = redis.call("GET", "seckill:stock:" .. exposeKey);
    if stock then
        local stockCount = tonumber(stock);
        if stockCount <= 0 then
            -- 库存不足
            return 4;
        end

        redis.call("DECR", "seckill:stock:" .. exposeKey);
        redis.call("HSET", "seckill:queue:" .. exposeKey, userId, token);
        -- 排队成功
        return -1;

    else
        -- 秒杀库存未找到
        return 2;

    end
end

local function checkToken(exposeKey, userId, token)
    local oldToken = redis.call("hget", "seckill:queue:" .. exposeKey, userId);
    if oldToken and (token == oldToken) then
        -- 排队过了
        return 5;
    end

    -- 没有排队
    return -1;
end

local function deleteToken(exposeKey, userId)
    redis.call("hdel", "seckill:queue:" .. exposeKey, userId)
    return 1;
end



local method = KEYS[1];
local exposeKey = ARGV[1];
local userId = ARGV[2];
local token = ARGV[3];

if method == 'setToken' then
    return setToken(exposeKey, userId, token);

elseif method == 'checkToken' then
    return checkToken(exposeKey, userId, token);

elseif method == 'deleteToken' then
    return deleteToken(exposeKey, userId);
else
    -- Lua方法不存在
    return 2;

end