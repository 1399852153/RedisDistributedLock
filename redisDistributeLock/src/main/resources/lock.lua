-- 获取参数
local requestIDKey = KEYS[1]
local lockCountKey = KEYS[2]

local currentRequestID = ARGV[1]
local expireTimeTTL = ARGV[2]

-- setnx 尝试加锁
local lockSet = redis.call('setnx',requestIDKey,currentRequestID)

if lockSet == 1
then
    -- 加锁成功 设置过期时间和重入次数
	redis.call('expire',requestIDKey,expireTimeTTL)
	redis.call('set',lockCountKey,1)
	redis.call('expire',lockCountKey,expireTimeTTL)
	return 1
else
    -- 判断是否是重入加锁
	local oldRequestID = redis.call('get',requestIDKey)
	if currentRequestID == oldRequestID
	then
	    -- 是重入加锁
		redis.call('incr',lockCountKey)
		-- 重置过期时间
		redis.call('expire',requestIDKey,expireTimeTTL)
		redis.call('expire',lockCountKey,expireTimeTTL)
		return 1
	else
	    -- requestID不一致，加锁失败
	    return 0
	end
end
