-- 获取参数
local requestIDKey = KEYS[1]

local currentRequestID = ARGV[1]
local expireTimeTTL = ARGV[2]

-- setnx 尝试加锁
local lockSet = redis.call('hsetnx',KEYS[1],'lockKey',currentRequestID)

if lockSet == 1
then
    -- 加锁成功 设置过期时间和重入次数=1
	redis.call('expire',KEYS[1],expireTimeTTL)
	redis.call('hset',KEYS[1],'lockCount',1)
	return 1
else
    -- 判断是否是重入加锁
	local oldRequestID = redis.call('hget',KEYS[1],'lockKey')
	if currentRequestID == oldRequestID
	then
	    -- 是重入加锁
		redis.call('hincrby',KEYS[1],'lockCount',1)
		-- 重置过期时间
		redis.call('expire',KEYS[1],expireTimeTTL)
		return 1
	else
	    -- requestID不一致，加锁失败
	    return 0
	end
end
