-- key：限流的键名，例如 "rate_limit:api_request"
-- max_requests：窗口时间内允许的最大请求次数
-- window_size：时间窗口大小（单位：秒）
-- current_time：当前时间戳（由 Redis 传递进来）
-- 返回值：如果未超过限流，返回 1；如果超过限流，返回 0
local key = KEYS[1]
local max_requests = tonumber(ARGV[1])
local window_size = tonumber(ARGV[2])
local current_time = tonumber(ARGV[3])

-- 移除过期的数据
redis.call('ZREMRANGEBYSCORE', key, '-inf', current_time - window_size)

-- 获取当前窗口的请求数量
local current_count = redis.call('ZCARD', key)

if not current_count or current_count < max_requests  then
    -- 如果请求数量未超过限制，将当前请求添加到 ZSET 中，并设置其分数为当前时间戳
    redis.call('ZADD', key, current_time, current_time)
    return current_count + 1
else
    -- 如果请求数量超过限制，返回 0 表示限流
    return 0
end
