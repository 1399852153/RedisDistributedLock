local requestIDKey = KEYS[1]
local lockCountKey = KEYS[2]

local currentRequestID = ARGV[1]

if redis.call('get', requestIDKey) == currentRequestID
then
	local currentCount = redis.call('decr',lockCountKey)
	if currentCount == 0
	then
	    redis.call('del',requestIDKey)
	    redis.call('del',lockCountKey)
	    return 1
	else
	    return 0 end
else 
	return 0 end