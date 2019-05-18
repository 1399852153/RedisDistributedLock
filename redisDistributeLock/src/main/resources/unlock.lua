-- 获取参数
local requestIDKey = KEYS[1]
local lockCountKey = KEYS[2]

local currentRequestID = ARGV[1]

-- 判断requestID一致性
if redis.call('get', requestIDKey) == currentRequestID
then
    -- requestID相同，重入次数自减
	local currentCount = redis.call('decr',lockCountKey)
	if currentCount == 0
	then
	    -- 重入次数为0，删除锁
	    redis.call('del',requestIDKey)
	    redis.call('del',lockCountKey)
	    return 1
	else
	    return 0 end
else 
	return 0 end