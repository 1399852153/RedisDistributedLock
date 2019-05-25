-- 获取参数
local requestIDKey = KEYS[1]

local currentRequestID = ARGV[1]

-- 判断requestID一致性
if redis.call('hget',KEYS[1],'lockKey') == currentRequestID
then
    -- requestID相同，重入次数自减
	local currentCount = redis.call('hincrby',KEYS[1],'lockCount',-1)
	if currentCount == 0
	then
	    -- 重入次数为0，删除锁
	    redis.call('del',KEYS[1])
	    return 1
	else
	    return 0 end
else 
	return 0 end