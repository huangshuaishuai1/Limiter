-- key：限流的键名，例如 "rate_limit:api_request"
-- max_requests：窗口时间内允许的最大请求次数
-- window_size：时间窗口的长度
-- 返回值：如果未超过限流，返回 当前请求数；如果超过限流，返回 0
local key = KEYS[1]
local max_requests = tonumber(ARGV[1])
local window_size = tonumber(ARGV[2])

local current_count = redis.call('GET', key)
if not current_count then
    redis.call('ADD', key, 1)
    redis.call('EXPIRE',key,window_size)
    return 1
end

if current_count and current_count < max_requests then
    redis.call('INCRBY',key,1)
    return current_count + 1;
else
    return 0
end
